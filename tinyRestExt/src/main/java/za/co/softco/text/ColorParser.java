/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Revision 1.2  2007/12/23 17:02:17  remjohn
 *  Fixed comments
 *
 *  Revision 1.1  2007/12/22 19:34:36  remjohn
 *  Added to CVS
 *
 *  Revision 1.2  2007/12/21 16:36:51  john
 *  Implemented allowMultiItemPrecast()
 *
 *  Revision 1.1  2007/12/01 12:24:01  john
 *  Added to CVS
 *
 *  Revision 1.3  2006/12/17 10:44:59  goofyxp
 *  Normalize string before parsing
 *
 *  Revision 1.2  2006/05/12 14:08:06  obelix
 *  Improve parsing and casting of dates, times and timestamps
 *
 *  Revision 1.1  2006/04/13 08:58:31  goofyxp
 *  Created
 *
 *******************************************************************************/
package za.co.softco.text;

import java.awt.Color;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import za.co.softco.util.Utils;

/**
 * Parse / format date & time values
 * @author john
 * @model
 */
public class ColorParser implements Parser<Color> {

    private final static Map<Color, String> names = new HashMap<Color, String>(20);
    private final static Map<String, Color> colors = new HashMap<String, Color>(20);

    static {
        final int public_static = Modifier.PUBLIC | Modifier.STATIC;
        for (Field f : Color.class.getDeclaredFields()) {
            if (f.getType() != Color.class)
                continue;
            if ((f.getModifiers() & public_static) != public_static)
                continue;
            try {
                String name = f.getName().toLowerCase();
                colors.put(name, (Color) f.get(null));
                names.put((Color) f.get(null), name);
            } catch (IllegalArgumentException e) {
                Logger.getLogger(ColorParser.class).debug(e.getMessage());
            } catch (IllegalAccessException e) {
                Logger.getLogger(ColorParser.class).debug(e.getMessage());
            }
        }
    }

    /*
     * @see za.co.softco.text.Parser#allowMultiItemPrecast()
     */
    @Override
    public boolean allowMultiItemPrecast() {
        return true;
    }

    /**
     * Get a color from a hex value
     * @param hex
     * @return
     * @throws ParseException
     */
    public static Color colorFromHex(String hex) throws ParseException {
        String tmp = hex;
        if (tmp.startsWith("0x"))
            tmp = tmp.substring(2);
        if (tmp.startsWith("#"))
            tmp = tmp.substring(1);
        if (tmp.endsWith("h"))
            tmp = tmp.substring(0, hex.length() - 1);
        if (tmp.length() > 6)
            throw new ParseException("Hex color representation can have at most 6 digits (" + hex + ")", 0);

        tmp = tmp.toUpperCase();
        int rgb = 0;
        for (int i = 0; i < tmp.length(); i++) {
            char c = tmp.charAt(i);
            if (c >= '0' && c <= '9')
                rgb = (rgb * 16) + c - '0';
            else if (c >= 'A' && c <= 'F')
                rgb = (rgb * 16) + c - 'A' + 10;
            else
                throw new ParseException("Illegal hex character (" + c + ") in " + hex, i);
        }
        return new Color(rgb);
    }

    /*
     * @see za.co.softco.io.DataParser#parseValue(java.lang.String)
     */
    @Override
    public Color parse(String value) throws ParseException {
        value = Utils.normalize(value);
        if (value == null)
            return null;
        Color result = colors.get(value.toLowerCase());
        if (result != null)
            return result;

        try {
            if (value.startsWith("0x") || value.startsWith("#") || value.endsWith("h"))
                return colorFromHex(value.substring(2));
        } catch (ParseException e) {
            Logger.getLogger(ColorParser.class).debug(e.getMessage());
        }

        boolean hex = true;
        boolean num = true;
        for (char c : value.toUpperCase().toCharArray()) {
            if (c < '0' || c > '9') {
                num = false;
                if (c < 'A' || c > 'F')
                    hex = false;
            }
        }
        if (hex) {
            try {
                return colorFromHex(value.substring(2));
            } catch (ParseException e) {
                Logger.getLogger(ColorParser.class).debug(e.getMessage());
            }
        }
        if (num)
            return new Color(IntegerParser.toInt(value));

        try {
            result = Color.getColor(value);
            if (result != null)
                return result;
            throw new ParseException("Could not parse color (" + value + ")", 0);
        } catch (Exception e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    /*
     * @see za.co.softco.parser.Parser#toString(java.lang.Object)
     */
    @Override
    public String toString(Object value) {
        if (value == null)
            return null;

        String result = names.get(value);
        if (result != null)
            return result;

        if (value instanceof Color)
            return "0x" + Utils.toHexString(((Color) value).getRGB(), 6);

        return value.toString();
    }

    /*
     * @see za.co.softco.parser.Parser#format(java.lang.Object, java.lang.String)
     */
    @Override
    public String format(Object value, String format) {
        if (value == null)
            return null;

        return toString(value);
    }

    /*
     * @see za.co.softco.text.Parser#cast(java.lang.Object)
     */
    @Override
    public Color cast(Object value) {
        if (value == null || value instanceof Color)
            return (Color) value;
        if (value instanceof Number)
            return new Color(((Number) value).intValue());
        try {
            return parse(value.toString());
        } catch (ParseException e) {
            Logger.getLogger(ColorParser.class).debug("Could not parse \"" + value + "\" as a Color");
            return null;
        }
    }
}
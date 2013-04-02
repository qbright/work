/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: BooleanParser.java,v $
 *  Revision 1.4  2007/12/23 17:02:17  remjohn
 *  Fixed comments
 *
 *  Revision 1.3  2007/12/22 19:35:14  remjohn
 *  Improve DataParser framework
 *
 *  Revision 1.2  2007/10/05 00:48:23  remjohn
 *  Refactor to avoid warnings
 *
 *  Revision 1.1  2007/08/15 13:05:58  rembrink
 *  Added to CVS
 *
 *  Revision 1.1  2007/08/05 08:31:11  john
 *  Converted base package to za.co.softco
 *
 *  Revision 1.1  2007/06/14 10:21:06  goofyxp
 *  Split besterBase from bester library
 *
 *  Revision 1.5  2007/04/07 17:03:28  goofyxp
 *  Use valueOf() in stead of new to minimize memory usage
 *
 *  Revision 1.4  2006/11/28 10:46:02  goofyxp
 *  Remove unnecessary casts
 *
 *  Revision 1.3  2006/03/18 19:53:25  obelix
 *  Restructure code to use Map<String, Object> in stead of Properties
 *
 *  Revision 1.2  2006/03/01 23:51:40  goofyxp
 *  Convert to Java 5 syntax
 *
 *  Revision 1.1  2006/01/10 14:58:47  goofyxp
 *  Renamed za.co.softco.parser package to za.co.softco.text
 *
 *  Revision 1.2  2005/12/05 09:23:57  obelix
 *  Add comments
 *  Implement new format(Object, String) function in Parser interface to throw a default exception
 *
 *  Created on 01-Dec-2005
 *******************************************************************************/
package za.co.softco.text;

import java.text.ParseException;
import java.util.Arrays;

import za.co.softco.util.Utils;

/**
 * Parse / format boolean values
 * @author john
 * @model
 */
public class BooleanParser implements Parser<Boolean> {

    private static final String[] positive = { "yes", "y", "true", "t", "1" };
    private static final String[] negative = { "no", "n", "false", "f", "0" };
    
    static {
        Arrays.sort(positive);
        Arrays.sort(negative);
    }
    
    /*
     * @see za.co.softco.text.Parser#allowMultiItemPrecast()
     */
    @Override
    public boolean allowMultiItemPrecast() {
        return true;
    }

    /*
     * @see za.co.softco.io.DataParser#parseValue(java.lang.String)
     */
    @Override
    public Boolean parse(String value) throws ParseException {
        return Boolean.valueOf((value != null ? Numbers.parseBoolean(value) : false));
    }

    /**
     * Convert an object to a boolean
     * @param value
     * @return
     * @throws ParseException
     * @model
     */
    public static final boolean toBoolean(Object value) {
        if (value == null)
            return false;
        if (value instanceof Boolean)
            return ((Boolean) value).booleanValue();
        if (value instanceof Number)
            return (((Number) value).longValue() != 0);
        try {
            return Numbers.parseBoolean(value.toString());
        } catch (ParseException e) {
            if (value instanceof String) {
                String s = value.toString().trim().toLowerCase();
                if (s.equals("on"))
                    return true;
                if (s.equals("yes") || s.equals("y"))
                    return true;
            }
            return false;
        }
    }

    /*
     * @see za.co.softco.parser.Parser#toString(java.lang.Object)
     */
    @Override
    public String toString(Object value) {
        return (value != null ? value.toString() : null);
    }

    /*
     * @see za.co.softco.parser.Parser#format(java.lang.Object, java.lang.String)
     */
    @Override
    public String format(Object value, String format) {
        throw new IllegalStateException("Not yet implemented");
    }

    /*
     * @see za.co.softco.text.Parser#cast(java.lang.Object)
     */
    @Override
    public Boolean cast(Object value) {
        if (value == null)
            return Boolean.FALSE;
        if (value instanceof Boolean)
            return (Boolean) value;
        if (value instanceof Number)
            return Boolean.valueOf((((Number) value).longValue() != 0));
        String sval = Utils.normalize(value.toString());
        if (sval == null)
            return Boolean.FALSE;
        sval = sval.toLowerCase();
        try {
            return Boolean.valueOf(Numbers.parseBoolean(sval));
        } catch (ParseException e) {
            if (Arrays.binarySearch(positive, sval) >= 0)
                return Boolean.TRUE;
            if (Arrays.binarySearch(negative, sval) >= 0)
                return Boolean.FALSE;
            throw new ClassCastException(e.getMessage());
        }
    }
}
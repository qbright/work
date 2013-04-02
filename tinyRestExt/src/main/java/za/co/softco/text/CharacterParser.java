/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: CharacterParser.java,v $
 *  Revision 1.3  2007/12/23 17:02:17  remjohn
 *  Fixed comments
 *
 *  Revision 1.2  2007/12/22 19:35:14  remjohn
 *  Improve DataParser framework
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
 *  Revision 1.4  2007/04/07 17:03:28  goofyxp
 *  Use valueOf() in stead of new to minimize memory usage
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

/**
 * Parse / format character values
 * @author john
 * @model
 */
public class CharacterParser implements Parser<Character> {

    /*
     * @see za.co.softco.text.Parser#allowMultiItemPrecast()
     */
    @Override
    public boolean allowMultiItemPrecast() {
        return true;
    }

    /**
     * Convert a value to a character
     * @param value
     * @return
     * @model
     */
    public static char toChar(Object value) {
        if (value instanceof Character)
            return ((Character) value).charValue();
        if (value instanceof String && ((String) value).length() > 0)
            return ((String) value).charAt(0);
        if (value instanceof Number)
            return (char) ((Number) value).intValue();
        if (value instanceof Boolean)
            return (((Boolean) value).booleanValue() ? 'T' : 'F');
        return '\0';
    }

    /*
     * @see za.co.softco.io.DataParser#parseValue(java.lang.String)
     */
    @Override
    public Character parse(String value) {
        if (value != null && value.length() > 0)
            return Character.valueOf(value.charAt(0));
        return null;
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
        if (value instanceof Character)
            return value.toString();
        throw new IllegalStateException("Invalid parameter type");
    }

    /*
     * @see za.co.softco.text.Parser#cast(java.lang.Object)
     */
    @Override
    public Character cast(Object value) {
        try {
            return Character.class.cast(value);
        } catch (ClassCastException e) {
            return parse(value.toString());
        }
    }
}
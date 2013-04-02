/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: ShortParser.java,v $
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
 *  Revision 1.5  2007/04/07 17:03:28  goofyxp
 *  Use valueOf() in stead of new to minimize memory usage
 *
 *  Revision 1.4  2006/12/13 06:40:24  obelix
 *  Improve parsing of numeric values to handle default number format (thousand separator)
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
 *  Revision 1.2  2005/12/05 09:25:29  obelix
 *  Add comments
 *  Implement new format(Object, String) function in Parser interface to throw a default exception
 *
 *  Created on 01-Dec-2005
 *******************************************************************************/
package za.co.softco.text;

import java.text.NumberFormat;
import java.text.ParseException;

import za.co.softco.util.Utils;

/**
 * Parse / format short values
 * @author john
 * @model
 */
public class ShortParser implements Parser<Short> {

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
    public Short parse(String value) throws ParseException {
        value = Utils.normalize(value);
        if (value == null)
            return null;
        try {
            return Short.valueOf(value);
        } catch (NumberFormatException e) {
            return cast(NumberFormat.getIntegerInstance().parse(value));
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
    public Short cast(Object value) {
        if (value instanceof Short)
            return (Short) value;
        if (value instanceof Number)
            return Short.valueOf(((Number) value).shortValue());
        try {
            return Short.class.cast(value);
        } catch (ClassCastException e) {
            return Short.valueOf((short) IntegerParser.toInt(value));
        }
    }

}
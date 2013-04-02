/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: DoubleParser.java,v $
 *  Revision 1.4  2007/12/23 17:02:17  remjohn
 *  Fixed comments
 *
 *  Revision 1.3  2007/12/22 19:35:14  remjohn
 *  Improve DataParser framework
 *
 *  Revision 1.2  2007/09/07 09:29:34  remjohn
 *  Apply changes from bester package
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
 *  Revision 1.7  2007/03/14 16:29:38  goofyxp
 *  Improve parsing
 *
 *  Revision 1.6  2006/12/17 10:59:52  goofyxp
 *  Use Number.doubleValue() in stead of Number.floatValue()
 *
 *  Revision 1.5  2006/12/13 06:40:24  obelix
 *  Improve parsing of numeric values to handle default number format (thousand separator)
 *
 *  Revision 1.4  2006/09/29 11:18:34  obelix
 *  Implemented format(Object, String)
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
 *  Revision 1.2  2005/12/05 09:24:48  obelix
 *  Add comments
 *  Implement new format(Object, String) function in Parser interface to throw a default exception
 *
 *  Created on 01-Dec-2005
 *******************************************************************************/
package za.co.softco.text;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.regex.Pattern;

import za.co.softco.util.Utils;

/**
 * Parse / format double values
 * @author john
 * @model
 */
public class DoubleParser implements Parser<Double> {
    private static final String PATTERN_COMMA = Pattern.quote(",");
    private static final String PATTERN_POINT = Pattern.quote(".");

    public static final boolean CONVERT_DECIMAL_COMMA;

    static {
        String temp = NumberFormat.getInstance().format(1.5);
        CONVERT_DECIMAL_COMMA = (temp.indexOf(',') > 0);
    }

    /*
     * @see za.co.softco.text.Parser#allowMultiItemPrecast()
     */
    @Override
    public boolean allowMultiItemPrecast() {
        return true;
    }

    /**
     * Get the best float value (no exception thrown)
     * @param value
     * @return
     * @model
     */
    public static double toDouble(Object value) {
        if (value == null)
            return 0;
        if (value instanceof Number)
            return ((Number) value).doubleValue();
        try {
            Double result = Double.class.cast(value);
            return (result != null ? result.doubleValue() : 0);
        } catch (ClassCastException e) {
            NumberFormat fmt = NumberFormat.getNumberInstance();
            fmt.setGroupingUsed(false);
            String svalue = value.toString().replaceAll(",", "");
            try {
                return fmt.parse(svalue).doubleValue();
            } catch (ParseException e2) {
                try {
                    return Double.parseDouble(value.toString());
                } catch (NumberFormatException e1) {
                    return 0;
                }
            }
        }
    }

    /**
     * Convert to have only a decimal point and no thousand seperators
     * @param value
     * @return
     * @throws ParseException 
     */
    public static String normalizeDouble(String value) throws ParseException {
        value = Utils.normalize(value);
        if (value == null)
            return null;
        int posComma = value.indexOf(',');
        int posPoint = value.indexOf('.');
        int lastComma = value.lastIndexOf(',');
        int lastPoint = value.lastIndexOf('.');
        if (lastComma >= 0 && lastPoint >= 0) {
            int prevComma = value.lastIndexOf(',', lastComma);
            int prevPoint = value.lastIndexOf('.', lastPoint);
            if (prevComma >= 0 && prevPoint >= 0) 
                throw new ParseException("Invalid number format", 0);
        }
        int ndx = 0;
        boolean neg = false;
        for (char c : value.toCharArray()) {
            try {
                switch (c) {
                case '-' :
                    if (neg)
                        throw new ParseException("Invalid number format", ndx);
                    neg = true;
                    if (ndx > 0 && ndx < value.length()-1)
                        throw new ParseException("Invalid number format", ndx);
                    break;
                case '.' :
                case ',' :
                    break;
                default :
                    if (c < '0' || c > '9')
                        throw new ParseException("Invalid number format", ndx);
                }
            } finally {
                ndx++;
            }
        }
        if (posComma != lastComma)
            return value.replaceAll(PATTERN_COMMA, "");
        if (posPoint != lastPoint)
            return value.replaceAll(PATTERN_POINT, "").replaceAll(PATTERN_COMMA, ".");
        if (posComma >= 0 && posPoint >= 0) {
            if (posPoint > 0)
                value = value.replaceAll(PATTERN_COMMA, "");
            else
                value = value.replaceAll(PATTERN_POINT, "");
            if (CONVERT_DECIMAL_COMMA)
                return value.replaceAll(PATTERN_COMMA, ".");
            return value;
        }
        if (posPoint >= 0)
            return value;
        if (posComma >= 0)
            return value.replaceAll(PATTERN_COMMA, ".");
        return value;
    }

    /*
     * @see za.co.softco.io.DataParser#parseValue(java.lang.String)
     */
    @Override
    public Double parse(String value) throws ParseException {
        value = normalizeDouble(value);
        if (value == null)
            return null;
        try {
            return Double.valueOf(value);
        } catch (NumberFormatException e1) {
            return cast(NumberFormat.getNumberInstance().parse(value));
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
        return new DecimalFormat(format).format(value);
    }

    /*
     * @see za.co.softco.text.Parser#cast(java.lang.Object)
     */
    @Override
    public Double cast(Object value) {
        if (value instanceof Double)
            return (Double) value;
        if (value instanceof Number)
            return new Double(((Number) value).doubleValue());
        try {
            return Double.class.cast(value);
        } catch (ClassCastException e) {
            try {
                return parse(value.toString());
            } catch (ParseException e1) {
                throw new ClassCastException(e1.getMessage());
            }
        }
    }
}
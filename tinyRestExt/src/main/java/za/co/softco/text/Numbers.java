/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: Numbers.java,v $
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
 *  Revision 1.1  2006/01/10 14:58:47  goofyxp
 *  Renamed za.co.softco.parser package to za.co.softco.text
 *
 *  Revision 1.2  2005/12/05 09:16:26  obelix
 *  Add comments
 *
 *  Created on 01-Dec-2005
 *******************************************************************************/
package za.co.softco.text;

import java.text.ParseException;

import za.co.softco.util.Captions;
import za.co.softco.util.Utils;

/**
 * This is a class with general static functions to handle numeric values
 * @author john
 * @model
 */
public class Numbers {

    /**
     * Parse a string as an integer.
     * @param value
     * @param defaultValue
     * @return
     */
    public static final int toInt(String value, int defaultValue) {
        value = Utils.normalize(value);
        if (value == null)
            return defaultValue;
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Parse a string as an long.
     * @param value
     * @return
     * @model
     */
    public static final long toLong(Object value) {
        if (value == null)
            return 0;
        if (value instanceof Number)
            return ((Number) value).longValue();
        try {
            return Long.parseLong(Utils.normalize(value.toString()));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /**
     * Do the best possible conversion from a value to an int
     * @param value
     * @return
     * @model
     */
    public static final int toInt(Object value) {
        return (int) toLong(value);
    }

    /**
     * Do the best possible conversion from a value to a byte
     * @param value
     * @return
     * @model
     */
    public static final byte toByte(Object value) {
        return (byte) toLong(value);
    }

    /**
     * Do the best possible conversion from a value to a double
     * @param value
     * @return
     * @model
     */
    public static final double toDouble(Object value) {
        if (value == null)
            return 0;
        if (value instanceof Number)
            return ((Number) value).doubleValue();
        if (value instanceof StringBuffer)
            value = value.toString();
        if (value instanceof String)
            return Numbers.parseFloat((String) value);
        return 0;
    }

    /**
     * Do the best possible conversion from a value to a float
     * @param value
     * @return
     * @model
     */
    public static final float toFloat(Object value) {
        return (float) toDouble(value);
    }

    /**
     * Parse a string as a float
     * @param value
     * @return
     * @model
     */
    public static float parseFloat(String value) {
        return (float) Numbers.parseDouble(value);
    }

    /**
     * Parse a string as a double
     * @param value
     * @return
     * @model
     */
    public static double parseDouble(String value) throws NumberFormatException {
        if (value != null && !value.trim().equals("")) {
            char chr;
            double factor = 1;
            double result = 0;
            boolean decimal = false;

            value = value.trim();

            for (int i = 0; i < value.length(); i++) {
                chr = value.charAt(i);
                switch (chr) {
                case '+':
                    if (i > 0)
                        throw new NumberFormatException(Captions.ERROR_INVALIDFLOATFORMAT + " (" + value + ")");
                    break;

                case '-':
                    if (i == 0)
                        factor = -1;
                    else
                        throw new NumberFormatException(Captions.ERROR_INVALIDFLOATFORMAT + " (" + value + ")");
                    break;

                case '.':
                    if (!decimal) {
                        decimal = true;
                        factor = factor * 0.1;
                    } else {
                        throw new NumberFormatException(Captions.ERROR_INVALIDFLOATFORMAT + " (" + value + ")");
                    }
                    break;

                default:
                    if (chr >= '0' && chr <= '9') {
                        if (decimal) {
                            result += factor * (byte) (chr - '0');
                            factor /= 10;
                        } else {
                            result = (result * 10) + (factor * (byte) (chr - '0'));
                        }
                    } else {
                        throw new NumberFormatException(Captions.ERROR_INVALIDFLOATFORMAT + " (" + value + ")");
                    }
                }
            }
            return result;
        }
        return 0;
    }

    /**
     * Parse a string as a boolean
     * @param value
     * @return
     * @model
     */
    public static final boolean parseBoolean(String value) throws ParseException {
        value = Utils.normalize(value);
        if (value == null)
            return false;

        value = value.toLowerCase();

        if (value.equals("true") | value.equals("yes") | value.equals("t") | value.equals("y") | value.equals("1"))
            return true;

        if (value.equals("false") | value.equals("no") | value.equals("f") | value.equals("n") | value.equals("0"))
            return false;

        throw new ParseException("Invalid boolean value (" + value + ")", 0);
    }

    /**
     * Do the best possible conversion from a value to a boolean
     * @param value
     * @return
     * @model
     */
    public static boolean toBoolean(Object value) {
        if (value instanceof Boolean)
            return ((Boolean) value).booleanValue();
        if (value instanceof Number)
            return (((Number) value).intValue() != 0);
        if (value != null)
            try {
                Object result = DataParser.parse(value.toString(), Boolean.class);
                if (result instanceof Boolean)
                    return ((Boolean) result).booleanValue();
            } catch (ParseException e) {
                // Default value (false) assumed
            }
        return false;
    }

    /**
     * Do the best possible conversion from a value to an integer
     * @param value
     * @return
     * @model
     */
    public static int toInteger(Object value) {
        if (value instanceof Number)
            return ((Number) value).intValue();
        if (value instanceof Boolean)
            return (value == Boolean.TRUE ? 1 : 0);
        if (value != null)
            try {
                Object result = DataParser.parse(value.toString(), Integer.class);
                if (result instanceof Number)
                    return ((Number) result).intValue();
            } catch (ParseException e) {
                // Default value (0) assumed
            }
        return 0;
    }

}

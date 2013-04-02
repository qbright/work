/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: TimeParser.java,v $
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import za.co.softco.util.Utils;

/**
 * Parse / format date & time values
 * @author john
 * @model
 */
public class TimeParser implements Parser<java.sql.Time> {

    /*
     * @see za.co.softco.text.Parser#allowMultiItemPrecast()
     */
    @Override
    public boolean allowMultiItemPrecast() {
        return true;
    }
    
    /**
     * Return the time of the day as a float (hour as integer part and minutes as fraction)
     * @return
     */
    public static float getTimeOfDay(int hour, int minute) {
        return (float) (hour + (minute / 60.0));
    }

    /**
     * Return the time of the day as a float (hour as integer part and minutes as fraction)
     * @return
     */
    public static float getTimeOfDay(java.util.Date timestamp) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(timestamp);
        return getTimeOfDay(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE));
    }
    
    /**
     * Strip the date part of a date value
     * @param value
     * @return
     */
    public static java.sql.Time stripDate(java.util.Date value) {
        if (value == null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(value);
        int hr = cal.get(Calendar.HOUR_OF_DAY);
        int min = cal.get(Calendar.MINUTE);
        int sec = cal.get(Calendar.SECOND);
        int ms = cal.get(Calendar.MILLISECOND);
        cal.clear();
        cal.set(Calendar.HOUR_OF_DAY, hr);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, sec);
        cal.set(Calendar.MILLISECOND, ms);
        return new java.sql.Time(cal.getTime().getTime());
    }

    /**
     * Static method for parsing time without date
     * @param value
     * @return
     * @throws ParseException
     */
    public static java.sql.Time parseTime(String value) throws ParseException {
        if (Utils.normalize(value) == null)
            return null;
        return parse(value, Utils.getDefaultTimeFormats());
    }

    /*
     * @see za.co.softco.io.DataParser#parseValue(java.lang.String)
     */
    @Override
    public java.sql.Time parse(String value) throws ParseException {
        return parseTime(value);
    }

    /*
     * @see za.co.softco.parser.Parser#toString(java.lang.Object)
     */
    @Override
    public String toString(Object value) {
        if (value == null)
            return null;
        Date time = cast(value);
        if (time == null)
            return null;
        return Utils.getDefaultTimeFormat().format(time);
    }

    /*
     * @see za.co.softco.parser.Parser#format(java.lang.Object, java.lang.String)
     */
    @Override
    public String format(Object value, String format) throws ParseException {
        if (value == null)
            return null;

        if (format == null)
            return toString(value);

        try {
            return new SimpleDateFormat(format).format(value);
        } catch (IllegalArgumentException e) {
            throw new ParseException("Invalid date format (" + format + ")", 0);
        }

    }

    /**
     * Static method for casting to a Time object
     * @param value
     * @return
     */
    public static final java.sql.Time toTime(Object value) {
        if (value == null)
            return null;

        if (value instanceof java.sql.Time)
            return (java.sql.Time) value;
        
        if (value instanceof Number)
            value = new java.util.Date(((Number) value).longValue());

        if (value instanceof java.util.Date)
            return stripDate((java.util.Date) value);

        try {
            return parseTime(value.toString());
        } catch (ParseException e1) {
            throw new ClassCastException("Could not convert " + value.getClass().getName() + " (" + value + ") to " + java.sql.Time.class.getName());
        }
    }

    /*
     * @see za.co.softco.text.Parser#cast(java.lang.Object)
     */
    @Override
   public java.sql.Time cast(Object value) {
        return toTime(value);
    }

    /**
     * Parse a string and return a date
     * @param value
     * @param inputFormats
     * @return
     * @throws ParseException
     */
    public static final java.sql.Time parse(String value, DateFormat[] inputFormats) throws ParseException {
        value = Utils.normalize(value);
        if (value == null)
            return null;

        Calendar cal = Calendar.getInstance();

        String message = null;
        for (int i = 0; i < inputFormats.length; i++) {
            try {
                cal.setTime(inputFormats[i].parse(value));
                return stripDate(cal.getTime());
            } catch (NumberFormatException e) {
                if (message == null)
                    message = e.getMessage();
            } catch (ParseException e) {
                if (message == null)
                    message = e.getMessage();
            }
        }
        throw new ParseException((message != null ? message : "Could not parse time (" + value + ")"), 0);
    }
}
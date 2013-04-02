/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: TimestampParser.java,v $
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
 *  Revision 1.2  2006/12/17 10:44:59  goofyxp
 *  Normalize string before parsing
 *
 *  Revision 1.1  2006/05/12 14:08:06  obelix
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

import za.co.softco.util.Utils;

/**
 * Parse / format date & time values
 * @author john
 * @model
 */
public class TimestampParser implements Parser<java.sql.Timestamp> {

    /*
     * @see za.co.softco.text.Parser#allowMultiItemPrecast()
     */
    @Override
    public boolean allowMultiItemPrecast() {
        return true;
    }

    /**
     * Strip the date part of a date value
     * @param date
     * @param time
     * @return
     */
    public static java.sql.Timestamp combine(java.util.Date date, java.util.Date time) {
        if (date == null && time == null)
            return null;

        // Default date to today
        date = DateParser.stripTime((date != null ? date : new java.util.Date()));

        // Default time to 00:00:00
        // Since time is already stripped away from date, date is used to get the default time
        time = TimeParser.stripDate((time != null ? time : date));

        // Get a calendar and fix the time by adding time zone offset
        Calendar cal = Calendar.getInstance();
        cal.setTime(new java.util.Date(date.getTime() + time.getTime()));
        cal.add(Calendar.MILLISECOND, cal.getTimeZone().getRawOffset());

        return new java.sql.Timestamp(cal.getTime().getTime());

        /*
         * Calendar tim = Calendar.getInstance(); tim.clear(); tim.setTime(time); Calendar cal = Calendar.getInstance(); cal.clear();
         * cal.setTime(date); cal.set(Calendar.HOUR_OF_DAY, tim.get(Calendar.HOUR_OF_DAY)); cal.set(Calendar.MINUTE, tim.get(Calendar.MINUTE));
         * cal.set(Calendar.SECOND, tim.get(Calendar.SECOND)); cal.set(Calendar.MILLISECOND, 0); return cal.getTime();
         */
    }

    /**
     * Static method for parsing timestamp
     * @param value
     * @return
     * @throws ParseException
     */
    public static final java.sql.Timestamp parseTimestamp(String value) throws ParseException {
        if (Utils.normalize(value) == null)
            return null;
        return parse(value, Utils.getDefaultTimestampFormats());
    }

    /*
     * @see za.co.softco.io.DataParser#parseValue(java.lang.String)
     */
    @Override
    public java.sql.Timestamp parse(String value) throws ParseException {
        if (value == null)
            return null;
        return parse(value, Utils.getDefaultTimestampFormats());
    }

    /*
     * @see za.co.softco.parser.Parser#toString(java.lang.Object)
     */
    @Override
    public String toString(Object value) {
        if (value == null)
            return null;
        value = cast(value);
        if (value == null)
            return null;
        return Utils.getDefaultTimestampFormat().format(value);
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
     * Static method for casting to a Timestamp object
     * @param value
     * @return
     */
    public static final java.sql.Timestamp toTimestamp(Object value) {
        if (value == null)
            return null;
        if (value instanceof java.sql.Timestamp)
            return (java.sql.Timestamp) value;
        if (value instanceof Number)
            value = new java.util.Date(((Number) value).longValue());
        if (value instanceof java.util.Date)
            return new java.sql.Timestamp(((java.util.Date) value).getTime());
        try {
            return parseTimestamp(value.toString());
        } catch (ParseException e) {
            throw new ClassCastException("Could not convert " + value.getClass().getName() + " (" + value + ") to " + java.sql.Timestamp.class.getName());
        }
    }

    /*
     * @see za.co.softco.text.Parser#cast(java.lang.Object)
     */
    @Override
    public java.sql.Timestamp cast(Object value) {
        return toTimestamp(value);
    }

    /**
     * Parse a string and return a date
     * @param value
     * @param inputFormats
     * @return
     * @throws ParseException
     */
    public static final java.sql.Timestamp parse(String value, DateFormat[] inputFormats) throws ParseException {
        value = Utils.normalize(value);
        if (value == null)
            return null;

        Calendar cal = Calendar.getInstance();
        // Calendar now = Calendar.getInstance();

        String message = null;
        for (int i = 0; i < inputFormats.length; i++) {
            try {
                cal.setTime(inputFormats[i].parse(value));
                return new java.sql.Timestamp(cal.getTime().getTime());
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
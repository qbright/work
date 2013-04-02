/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: DateParser.java,v $
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
 *  Revision 1.5  2006/12/17 10:44:59  goofyxp
 *  Normalize string before parsing
 *
 *  Revision 1.4  2006/05/12 14:08:06  obelix
 *  Improve parsing and casting of dates, times and timestamps
 *
 *  Revision 1.3  2006/03/18 19:53:25  obelix
 *  Restructure code to use Map<String, Object> in stead of Properties
 *
 *  Revision 1.2  2006/03/10 07:44:08  goofyxp
 *  Added generics
 *
 *  Revision 1.1  2006/01/10 14:58:47  goofyxp
 *  Renamed za.co.softco.parser package to za.co.softco.text
 *
 *  Revision 1.3  2005/12/05 11:24:34  obelix
 *  Add exception handling
 *
 *  Revision 1.2  2005/12/05 09:24:30  obelix
 *  Add comments
 *  Implement new format(Object, String) function in Parser interface to use SimpleDateFormat
 *
 *  Created on 01-Dec-2005
 *******************************************************************************/
package za.co.softco.text;

import static java.util.Calendar.YEAR;

import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import za.co.softco.util.Utils;

/**
 * Parse / format date & time values
 * @author john
 * @model
 */
public class DateParser implements Parser<java.sql.Date> {

    /*
     * @see za.co.softco.text.Parser#allowMultiItemPrecast()
     */
    @Override
    public boolean allowMultiItemPrecast() {
        return true;
    }

    /**
     * Strip the time from a date
     * @param value
     * @return
     */
    public static java.sql.Date stripTime(java.util.Date value) {
        if (value == null)
            return null;
        Calendar cal = Calendar.getInstance();
        cal.setTime(value);
        int year = cal.get(Calendar.YEAR);
        int mon = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);
        cal.clear();
        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.MONTH, mon);
        cal.set(Calendar.DAY_OF_MONTH, day);
        return new java.sql.Date(cal.getTime().getTime());
    }

    /**
     * Strip the time from a date
     * @param value
     * @return
     */
    public static long getTimeOfDay(java.util.Date value) {
        if (value == null)
            return 0;
        java.util.Date date = stripTime(value);
        return value.getTime() - (date != null ? date.getTime() : 0); 
    }

    /**
     * Determines whether a character is a numeric character
     * @param c
     * @return
     */
    protected static boolean isNumeric(char c) {
        return (c >= '0' && c <= '9');
    }

    /**
     * Determines whether a character is an alphabetical character
     * @param c
     * @return
     */
    protected static boolean isAlpha(char c) {
        return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
    }

    /**
     * Determines whether a character is white space
     * @param c
     * @return
     */
    protected static boolean isSpace(char c) {
        return (c == ' ' || c == '\t');
    }

    /*
     * @see za.co.softco.io.DataParser#parseValue(java.lang.String)
     */
    public static final java.sql.Date parseDate(String value) throws ParseException {
        if (Utils.normalize(value) == null)
            return null;
        return parse(value, Utils.getDefaultDateFormats());
    }

    /*
     * @see za.co.softco.io.DataParser#parseValue(java.lang.String)
     */
    @Override
    public java.sql.Date parse(String value) throws ParseException {
        return parseDate(value);
    }

    /*
     * @see za.co.softco.parser.Parser#toString(java.lang.Object)
     */
    @Override
    public String toString(Object value) {
        return Utils.formatDate(cast(value));
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
     * Static method for casting to a Date object
     * @param value
     * @return
     */
    public static final java.sql.Date toDate(Object value) {
        if (value == null)
            return null;
        if (value instanceof java.sql.Date)
            return (java.sql.Date) value;
        if (value instanceof Number)
            value = new java.util.Date(((Number) value).longValue());
        if (value instanceof java.util.Date)
            return new java.sql.Date(((java.util.Date) value).getTime());
        try {
            return parseDate(value.toString());
        } catch (ParseException e) {
            throw new ClassCastException("Could not convert " + value.getClass().getName() + " (" + value + ") to " + java.sql.Date.class.getName());
        }
    }

    /*
     * @see za.co.softco.text.Parser#cast(java.lang.Object)
     */
    @Override
    public java.sql.Date cast(Object value) {
        return toDate(value);
    }

    /**
     * Normalize a date represented as a string
     * @param value
     * @return
     */
    protected static String normalizeDate(String value) {
        if (value == null)
            return null;
        value = value.trim();
        if (value.equals(""))
            return null;
        char lastCh = value.charAt(0);
        StringBuffer result = new StringBuffer();
        result.append(lastCh);
        for (int i = 1; i < value.length(); i++) {
            char c = value.charAt(i);
            if ((isNumeric(lastCh) && isAlpha(c)) || (isAlpha(lastCh) && isNumeric(c))) {
                result.append(' ');
                lastCh = ' ';
            }
            if (!isSpace(lastCh) || !isSpace(c))
                result.append(c);
            lastCh = c;
        }
        return result.toString();
    }

    /**
     * Return the day of the week of a specific date
     * @param cal
     * @return
     */
    public static final String getDayOfWeek(Calendar cal) {
        return (cal != null ? getDayOfWeek(cal.get(Calendar.DAY_OF_WEEK)) : null);
    }

    /**
     * Get the name of the day of the week given a number from 0 to 7
     * @param day
     * @return
     */
    public static final String getDayOfWeek(int day) {
        if (Utils.symbols == null)
            Utils.symbols = new DateFormatSymbols();
        int offset = Math.min(Calendar.SUNDAY, Calendar.MONDAY);
        if (day - offset >= 7 || day - offset < 0)
            return null;

        return Utils.symbols.getWeekdays()[day - offset];
    }

    /**
     * Get the name of a month
     * @param cal
     * @return
     * @model
     */
    public static final String getMonth(Calendar cal) {
        return (cal != null ? getMonth(cal.get(Calendar.MONTH)) : null);
    }

    /**
     * Get the name of a month
     * @param month
     * @return
     */
    public static final String getMonth(int month) {
        if (Utils.symbols == null)
            Utils.symbols = new DateFormatSymbols();
        if (month < Calendar.JANUARY || month > Calendar.DECEMBER)
            return null;
        return Utils.symbols.getMonths()[month];
    }

    /**
     * Parse a string and return a date
     * @param value
     * @param inputFormats
     * @return
     * @throws ParseException
     */
    public static final java.sql.Date parse(String value, DateFormat[] inputFormats) throws ParseException {
        value = DateParser.normalizeDate(value);
        if (value == null)
            return null;

        Calendar cal = Calendar.getInstance();
        Calendar now = Calendar.getInstance();
        now.set(Calendar.HOUR, 0);
        now.set(Calendar.MINUTE, 0);
        now.set(Calendar.SECOND, 0);
        now.set(Calendar.MILLISECOND, 0);

        String message = null;
        for (int i = 0; i < inputFormats.length; i++) {
            try {
            	DateFormat fmt = inputFormats[i];
            	Date val = fmt.parse(value);
            	
                cal.setTime(val);

                switch (i) {
                case 1:
                case 3:
                    cal.set(YEAR, now.get(YEAR));
                    break;
                }
                cal.set(Calendar.HOUR, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                return new java.sql.Date(cal.getTime().getTime());
            } catch (NumberFormatException e) {
                if (message == null)
                    message = e.getMessage();
            } catch (ParseException e) {
                if (message == null)
                    message = e.getMessage();
            }
        }
        try {
            int day = Integer.parseInt(value);
            if (day < 1)
                throw new ParseException(value + " is not a valid day the month", 0);

            String month = DateParser.getMonth(now.get(Calendar.MONTH));
            switch (now.get(Calendar.MONTH)) {
            case Calendar.FEBRUARY:
                int year = now.get(Calendar.YEAR);
                if (year % 4 == 0 && year % 400 != 0) {
                    if (day > 29)
                        throw new ParseException(value + " is not a valid day of " + month + " " + year, 0);
                } else {
                    if (day > 28)
                        throw new ParseException(value + " is not a valid day of " + month + " " + year, 0);
                }
                break;

            case Calendar.JANUARY:
            case Calendar.MARCH:
            case Calendar.MAY:
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.OCTOBER:
            case Calendar.DECEMBER:
                if (day > 31)
                    throw new ParseException(value + " is not a valid day of " + month, 0);
                break;

            case Calendar.APRIL:
            case Calendar.JUNE:
            case Calendar.SEPTEMBER:
            case Calendar.NOVEMBER:
                if (day > 30)
                    throw new ParseException(value + " is not a valid day of " + month, 0);
                break;

            default:
                if (day > 31)
                    throw new ParseException(value + " is not a valid day of the month", 0);
            }
            now.set(Calendar.DAY_OF_MONTH, day);
            return new java.sql.Date(now.getTime().getTime());
        } catch (NumberFormatException e) {
            throw new ParseException(value + " is not a valid date.", 0);
        }
    }

	public static long timestampFromUnixEpoch(long unixepoch) {
		if (unixepoch == 0)
			return 0;
		return (unixepoch * 1000) - TimeZone.getDefault().getRawOffset();
	}

	public static long unixEpochFromTimestamp(long timestampMS) {
		if (timestampMS == 0)
			return 0;
		return ((timestampMS + TimeZone.getDefault().getRawOffset()) / 1000);
	}

	public static long dateToUnixEpoch(Date date) {
		if (date == null)
			return 0;
		return unixEpochFromTimestamp(date.getTime());
	}

	public static Date unixEpochToDate(long unixepoch) {
		if (unixepoch == 0)
			return null;
		return new Date(timestampFromUnixEpoch(unixepoch));
	}

	public static int unixEpochIntervalToDays(long unixepoch) {
        if (unixepoch == 0)
            return 0;
        return (int) Math.round(unixepoch / 24/ 60 / 60);
    }
}
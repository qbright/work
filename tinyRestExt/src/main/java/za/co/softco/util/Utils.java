/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library 
 * Description: Library classes
 * 
 * Changelog  
 *  $Log: Utils.java,v $
 *  Revision 1.5  2007/12/22 19:37:34  remjohn
 *  Update classes to reflect latest functionality in Bester package
 *
 *  Revision 1.4  2007/11/26 09:54:10  remjohn
 *  Added rethrow(Exception) method
 *
 *  Revision 1.3  2007/10/05 00:49:05  remjohn
 *  Refactor to avoid warnings
 *
 *  Revision 1.2  2007/09/07 09:34:08  remjohn
 *  Used Java5 syntax in loop
 *
 *  Revision 1.1  2007/08/15 13:05:58  rembrink
 *  Added to CVS
 *
 *  Revision 1.1  2007/08/05 08:31:11  john
 *  Converted base package to za.co.softco
 *
 *  Revision 1.1  2007/06/14 10:21:24  goofyxp
 *  Split besterBase from bester library
 *
 *  Revision 1.25  2007/04/07 16:58:51  goofyxp
 *  Get the best possible message from an exception
 *
 *  Revision 1.24  2007/02/02 21:43:14  obelix
 *  Added toEscapedAsciiString(byte[])
 *
 *  Revision 1.23  2006/12/28 10:24:09  obelix
 *  Added <?> to avoid generics warning
 *
 *  Revision 1.22  2006/11/28 13:05:43  obelix
 *  Fix bug
 *  Suppress switch warning
 *
 *  Revision 1.21  2006/10/20 14:53:47  obelix
 *  Make round(Date, datePart) thread safe
 *
 *  Revision 1.20  2006/10/10 10:05:46  goofyxp
 *  Added setDefaultExtension()
 *
 *  Revision 1.19  2006/07/11 07:46:54  goofyxp
 *  Added getShortClassName(Object)
 *
 *  Revision 1.18  2006/07/04 11:29:43  obelix
 *  Format code
 *  Implemented regularExpression
 *  Use Pattern to replace text
 *
 *  Revision 1.17  2006/06/14 08:35:42  goofyxp
 *  Renamed log functions to use the commons logging standards
 *
 *  Revision 1.16  2006/06/02 18:45:09  goofyxp
 *  Fix time formats
 *
 *  Revision 1.15  2006/05/20 16:53:27  obelix
 *  Moved resource functionality from Utils to Resource
 *
 *  Revision 1.14  2006/05/12 14:08:37  obelix
 *  Improve parsing and casting of dates, times and timestamps
 *
 *  Revision 1.13  2006/04/27 22:37:21  obelix
 *  Fix error output
 *
 *  Revision 1.12  2006/04/13 08:58:56  goofyxp
 *  Added time formats
 *
 *  Revision 1.11  2006/03/18 19:52:39  obelix
 *  Restructure code to use Map<String, Object> in stead of Properties
 *
 *  Revision 1.10  2006/03/01 23:51:40  goofyxp
 *  Convert to Java 5 syntax
 *
 *  Revision 1.9  2006/02/26 21:56:51  goofyxp
 *  Fix comment
 *
 *  Revision 1.8  2006/02/12 20:34:07  goofyxp
 *  Added toLong(Object) method
 *
 *  Revision 1.7  2006/01/10 15:56:19  goofyxp
 *  Renamed za.co.softco.parser package to za.co.softco.text
 *
 *  Revision 1.6  2006/01/03 20:48:36  obelix
 *  Added comments
 *  Deleted unused functions
 *
 *  Revision 1.5  2005/12/11 09:24:15  obelix
 *  Create functions round(Date) and round(Date, int)
 *  Import Calendar statically
 *
 *  Revision 1.4  2005/12/01 10:34:54  obelix
 *  Change comment
 *
 *  Revision 1.3  2005/12/01 10:32:42  obelix
 *  Add comments
 *  Add toInt(Object) function
 *
 *  Created on 1-Jul-2001
 *******************************************************************************/
package za.co.softco.util;

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.HOUR;
import static java.util.Calendar.HOUR_OF_DAY;
import static java.util.Calendar.JANUARY;
import static java.util.Calendar.MILLISECOND;
import static java.util.Calendar.MINUTE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.SECOND;
import static java.util.Calendar.YEAR;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.geom.Dimension2D;
import java.awt.geom.Rectangle2D;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.InterfaceAddress;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.security.AccessControlException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Pattern;

import javax.swing.UIManager;

import org.apache.log4j.Logger;

/**
 * This class contains static function so simplify every day coding
 * @author John Bester
 * @version 1.0
 */

public class Utils {
    public static final String ICON_SIZE_NORMAL = null;
    public static final String ICON_SIZE_SMALL = "small";
    public static final String DEFAULT_VERSION = "0.0.0.0";

    private static NumberFormat floatFormat = NumberFormat.getNumberInstance();
    public static final String DRIVECHAR = ":";
    private static String prefferredInetAddress = null;
    private static SimpleDateFormat[] defaultDateFormats;
    private static SimpleDateFormat[] defaultTimeFormats;
    private static SimpleDateFormat[] defaultTimestampFormats;
    private static DateFormat defaultDateFormat;
    private static DateFormat defaultTimeFormat;
    private static DateFormat defaultTimestampFormat;
    public static DateFormatSymbols symbols;
    private static int getVersionErrorCount = 0;

    private static final String[] ASCII_CODES = {    
        "<NULL>", "<SOH>", "<STX>",  "<ETX>", "<EOT>",
        "<ENQ>",  "<ACK>", "<BELL>", "<BS>",  "<TAB>",
        "<LF>",   "<VT>",  "<FF>",   "<CR>",  "<SO>",
        "<SI>",   "<DLE>", "<DC1>",  "<DC2>", "<DC3>",
        "<DC4>",  "<NAK>", "<SYN>",  "<ETB>", "<CAN>",
        "<EM>",   "<SUB>", "<ESC>",  "<FS>",  "<GS>",
        "<RS>",   "<US>" };

    /**
     * Return the default date format used by GUI applications
     * @return
     */
    public static DateFormat getDefaultDateFormat() {
        if (defaultDateFormat == null)
            defaultDateFormat = new SimpleDateFormat("d MMM yyyy");
        return defaultDateFormat;
    }

    /**
     * Return the default date format used by GUI applications
     * @return
     */
    public static DateFormat getDefaultTimeFormat() {
        if (defaultTimeFormat == null)
            defaultTimeFormat = new SimpleDateFormat("HH:mm:ss");
        return defaultTimeFormat;
    }

    /**
     * Return the default date & time format used by GUI applications
     * @return
     */
    public static DateFormat getDefaultTimestampFormat() {
        if (defaultTimestampFormat == null)
            defaultTimestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return defaultTimestampFormat;
    }

    /**
     * Return the default date & time format used by GUI applications
     * @return
     */
    public static DateFormat[] getDefaultTimestampFormats() {
        if (defaultTimestampFormats == null) {
            SimpleDateFormat[] formats = new SimpleDateFormat[] {
                    new SimpleDateFormat("y-MM-d h:m:s.S a"), // Default format checked first
                    new SimpleDateFormat("y-MM-d h:m:s a"),
                    new SimpleDateFormat("y-MM-d H:m:s.S"), // Default format checked first
                    new SimpleDateFormat("y-MM-d H:m:s"), new SimpleDateFormat("y-MMMM-d h:m:s.S a"), new SimpleDateFormat("y-MMMM-d h:m:s a"), new SimpleDateFormat("y-MMMM-d H:m:s.S"),
                    new SimpleDateFormat("y-MMMM-d H:m:s"), new SimpleDateFormat("y-MMM-d h:m:s.S a"), new SimpleDateFormat("y-MMM-d h:m:s a"), new SimpleDateFormat("y-MMM-d H:m:s.S"),
                    new SimpleDateFormat("y-MMM-d H:m:s"), new SimpleDateFormat("y-M-d h:m:s.S a"), new SimpleDateFormat("y-M-d h:m:s a"), new SimpleDateFormat("y-M-d H:m:s.S"),
                    new SimpleDateFormat("y-M-d H:m:s") };
            for (SimpleDateFormat fmt : formats)
            	fmt.setLenient(false);
            defaultTimestampFormats = formats;
        }

        return defaultTimestampFormats;
    }

    /**
     * Return a list of acceptable date formats used for input in GUI applications
     * @return
     */
    public static SimpleDateFormat[] getDefaultDateFormats() {
        if (defaultDateFormats == null) {
            SimpleDateFormat[] formats = { new SimpleDateFormat("d/MM/y"), new SimpleDateFormat("d/MM"), new SimpleDateFormat("d MMM y"), new SimpleDateFormat("d MMM"), new SimpleDateFormat("y-MM-d") };
            for (SimpleDateFormat fmt : formats)
            	fmt.setLenient(false);
            defaultDateFormats = formats;
        }
        return defaultDateFormats;
    }

    /**
     * Return a list of acceptable date formats used for input in GUI applications
     * @return
     */
    public static SimpleDateFormat[] getDefaultTimeFormats() {
        if (defaultTimeFormats == null) {
            SimpleDateFormat[] formats = { new SimpleDateFormat("K:m:s.S a"), new SimpleDateFormat("K:m:s a"), new SimpleDateFormat("K:m a"), new SimpleDateFormat("K a"),
                    new SimpleDateFormat("H:m:s.S"), new SimpleDateFormat("H:m:s"), new SimpleDateFormat("H:m"), new SimpleDateFormat("H") };
            for (SimpleDateFormat fmt : formats)
            	fmt.setLenient(false);
            defaultTimeFormats = formats;
        }
        return defaultTimeFormats;
    }

    /**
     * Format an IP address with a socket as a string (socket address)
     * @param address
     * @return
     */
    public static final String toString(InetSocketAddress address) {
        return toString(address.getAddress(), address.getPort());
    }

    /**
     * Format an IP address
     * @param address
     * @return
     */
    public static final String toString(InetAddress address) {
        if (address == null)
            return "null";

        byte[] ipAddress = address.getAddress();
        if (ipAddress == null)
            return "null";

        StringBuffer result = new StringBuffer();
        for (int b : ipAddress) {
            if (result.length() > 0)
                result.append('.');
            result.append(b >= 0 ? b : 0x100 + b);
        }

        return result.toString();
    }

    /**
     * Format a socket address (IP address and port number)
     * @param address
     * @param portNo
     * @return
     */
    public static final String toString(InetAddress address, int portNo) {
        if (address == null) {
            try {
                address = InetAddress.getLocalHost();
            } catch (IOException e) {
                return "localhost:" + portNo;
            }
        }

        byte[] ipAddress = address.getAddress();
        if (ipAddress == null)
            return "null";

        StringBuffer result = new StringBuffer();
        for (int i = 0; i < ipAddress.length; i++) {
            result.append(ipAddress[i] >= 0 ? ipAddress[i] : 0x100 + ipAddress[i]);
            if (i < 3)
                result.append(".");
        }
        result.append(":");
        result.append(portNo);
        return result.toString();
    }

    /**
     * Convert a socket to a string by formatting it as a socket address (IP:port)
     * @param socket
     * @return
     */
    public static final String toString(Socket socket) {
        if (socket == null)
            return "null";

        InetAddress address = socket.getInetAddress();
        if (address == null)
            return "null";

        byte[] ipAddress = socket.getInetAddress().getAddress();
        if (ipAddress == null)
            return "null";

        return toString(socket.getInetAddress(), socket.getLocalPort());
    }

    /**
     * Append the hex of a value to a string buffer
     * @param result
     * @param value
     * @param digits
     */
    public static void appendHex(StringBuffer result, long value, int digits) {
        if (result == null)
            throw new IllegalArgumentException("StringBuffer parameter required");
        if (digits <= 0)
            throw new IllegalArgumentException("Number of digits must be greater than 0");
        char[] hex = new char[digits];
        for (int i=0; i<digits; i++) {
            short x = (short) (value % 0x10);
            if (x > 9)
                hex[i] = (char) ('a' + x - 10);
            else
                hex[i] = (char) ('0' + x);
            value = value >> 4;
        }
        for (int i=digits-1; i>=0; i--)
            result.append(hex[i]);
    }
    
    /**
     * Create a regular expression from normal text
     * @param text
     * @return
     */
    public static String regularExpression(String text) {
       if (text == null)
           return null;
       StringBuffer result = new StringBuffer();
       for (int i=0; i<text.length(); i++) {
           char c = text.charAt(i);
           switch (c) {
           case '\t' :
               result.append("\\t");
               break;
           case '\n' :
               result.append("\\n");
               break;
           case '\r' :
               result.append("\\r");
               break;
           case '\f' :
               result.append("\\f");
               break;
           default :
               if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                   result.append(c);
               } else if (c > 0xFF) {
                   result.append("\\u");
                   appendHex(result, c, 4);
               } else {
                   result.append("\\x");
                   appendHex(result, c, 2);
               }
           }
       }
       return result.toString();
    }
    
    /**
     * Replace text in a string
     * @param source - Original text to search for occurrences of oldText
     * @param oldText - Text to search for
     * @param newText - Text to replace found instances of oldText
     * @param ignoreCase - Should search be case sensitive?
     * @param replaceAll - Replace all or replace only the first instance
     * @param regularExpression - Search criteria is already a regular expression
     * @return
     */
    public static String replaceString(String source, String oldText, String newText, boolean ignoreCase, boolean replaceAll, boolean regularExpression) {
        if (source == null || oldText == null)
            return source;
        
        if (newText == null)
            newText = "";
        
        if (!regularExpression) 
            oldText = regularExpression(oldText);
        
        Pattern p = (ignoreCase ? Pattern.compile(oldText, Pattern.CASE_INSENSITIVE) : Pattern.compile(oldText));
        if (replaceAll)
            return p.matcher(source).replaceAll(newText);
        return p.matcher(source).replaceFirst(newText);
    }

    /**
     * Return the size of text from a Graphics object
     * @param graphics
     * @param text
     * @return
     * @model
     */
    public Dimension getTextSize(Graphics graphics, String text) {
        FontMetrics met = graphics.getFontMetrics();
        return new Dimension(met.stringWidth(text), met.getHeight());
    }

    /**
     * Format an amount with 2 decimal places and a currency
     * @param cost
     * @param currency
     * @return
     */
    public static String formatCost(double cost, String currency) {
        floatFormat.setMinimumFractionDigits(2);
        floatFormat.setMaximumFractionDigits(2);
        return currency + " " + floatFormat.format(cost);
    }

    /**
     * Format an amount with 2 decimal places and the default currency (R)
     * @param cost
     * @param currency
     * @return
     */
    public static String formatCost(double cost) {
        return formatCost(cost, Captions.CURRENCY_R);
    }

    /**
     * Format a weight with a specific accuracy and unit
     * @param weight
     * @param decimals
     * @param unit
     * @return
     */
    public static String formatWeight(double weight, int decimals, String unit) {
        floatFormat.setMinimumFractionDigits(decimals);
        floatFormat.setMaximumFractionDigits(decimals);
        return floatFormat.format(weight) + " " + unit;
    }

    /**
     * Format a weight with a specified number of decimals and kg as the unit
     * @param weight
     * @param decimals
     * @return
     */
    public static String formatWeight(double weight, int decimals) {
        return formatWeight(weight, decimals, Captions.WEIGHT_KG);
    }

    /**
     * Format a weight with default 2 decimals and kg as the unit
     * @param weight
     * @return
     */
    public static String formatWeight(double weight) {
        return formatWeight(weight, 2, Captions.WEIGHT_KG);
    }

    /**
     * Format a quantity
     * @param quantity
     * @param decimals
     * @return
     */
    public static String formatQuantity(double quantity, int decimals) {
        double pow = Math.pow(10, decimals);
        quantity = Math.round(quantity * pow) / pow;

        long tempQty = Math.round(quantity * 10.0);

        if (tempQty % 10 != 0)
            return Double.toString(quantity);
        return Long.toString(tempQty / 10);
    }

    /**
     * Format an area in square meter
     * @param area
     * @return
     */
    public static String formatArea(double area) {
        return formatQuantity(area, 1) + " " + Captions.AREA_SQM;
    }

    /**
     * Clear a byte buffer by writing 0 to all array elements
     * @param buffer
     */
    public static final void clear(byte[] buffer) {
        if (buffer == null)
            return;
        for (int i = 0; i < buffer.length; i++)
            buffer[i] = (byte) 0;
    }

    /**
     * Copy a section of a byte array printing only an error message when bounds are exceeded
     * @param source
     * @param start
     * @param length
     * @return
     */
    public static final byte[] copy(byte[] source, int start, int length) {
        if (length <= 0)
            return new byte[0];

        byte[] result = new byte[length];
        try {
            System.arraycopy(source, start, result, 0, length);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Convert a signed byte to an unsigned byte and then to a short
     * @param val
     * @return
     */
    public static final short toShort(byte val) {
        return (val >= 0 ? (short) val : (short) (0x100 + val));
    }

    /**
     * Build a short from a low and a high byte that are both treated as unsigned
     * @param high
     * @param low
     * @return
     */
    public static final short toShort(byte high, byte low) {
        return (short) ((toShort(high) * 0x100) + toShort(low));
    }

    /**
     * Build a short from a low and a high short which are both truncated to unsigned bytes
     * @param high
     * @param low
     * @return
     */
    public static final short toShort(short highByte, short lowByte) {
        return (short) (((highByte % 0x100) * 0x100) + (lowByte % 0x100));
    }

    /**
     * Create an unsigned integer from a high and a low byte
     * @param high
     * @param low
     * @return
     */
    public static final int toInt(byte high, byte low) {
        return ((toShort(high) * 256) + toShort(low));
    }

    /**
     * Create an unsigned long value from 4 bytes that are all treated as unsigned bytes
     * @param b3
     * @param b2
     * @param b1
     * @param b0
     * @return
     * @model
     */
    public static final long toLong(byte b3, byte b2, byte b1, byte b0) {
        return ((((((long) toShort(b3) * 256) + toShort(b2)) * 256) + toShort(b1)) * 256) + toShort(b0);
    }

    /**
     * Build up a 2 digit hex string from a byte that is treated as unsigned
     * @param value
     * @return
     */
    public static final String toHexString(byte value) {
        if (value >= 0)
            return toHexString(value, 2);
        return toHexString(0x100 + value, 2);
    }

    /**
     * Format a byte array using ASCII codes
     * @param data
     * @return
     */
    public static String toEscapedAsciiString(byte[] data) {
        StringBuffer result = new StringBuffer();
        for (int i=0; i<data.length; i++) {
            byte b = data[i];
            if (b >= 0 && b < ASCII_CODES.length) {
                result.append(ASCII_CODES[b]);
                continue;
            } 
            char c = (char) data[i];
            if (c >= ' ' && c <= '~') {
                result.append(c);
                continue;
            }
            result.append('<');
            result.append(Integer.toString(Utils.toShort(data[i])));
            result.append('>');
        }
        return result.toString();
    }
    
    /**
     * Build up a 8 digit hex string from a long / unsigned integer
     * @param value
     * @param digits
     * @return
     */
    public static final String toHexString(long value, int digits) {
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < digits; i++) {
            int digit = (int) (value & 0xF);
            value = value >>> 4;
            char ch = (char) ('0' + digit);
            if (digit > 9)
                ch = (char) ('a' + digit - 10);

            result.insert(0, ch);
        }
        return result.toString();
    }

    /**
     * Convert a byte array to a hex string
     * @param data
     * @param start
     * @param length
     * @return
     */
    public static final String toHexString(byte[] data, int start, int length, boolean separateBytes) {
        if (data == null)
            return "(null)";

        StringBuffer result = new StringBuffer();
        start = Math.max(start, 0);
        length = Math.min(start + length, data.length);
        for (int i = start; i < length; i++) {
            result.append(toHexString(data[i]));
            if (separateBytes && i < length - 1)
                result.append(" ");
        }
        String text = result.toString();
        return (text != null ? text : "(empty)");
    }

    /**
     * Convert a byte array to a hex string
     * @param data
     * @param length
     * @return
     */
    public static final String toHexString(byte[] data, int length) {
        return toHexString(data, 0, length, false);
    }

    /**
     * Convert a byte array to a hex string
     * @param data
     * @return
     */
    public static final String toHexString(byte[] data) {
        if (data == null)
            return null;
        return toHexString(data, 0, data.length, false);
    }
    
    /**
     * Convert a long to a byte array
     * @param value
     * @return
     */
    public static byte[] toByteArray(long value) {
        byte[] b = new byte[8];
        for (int i = 0; i < 8; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }    
    
    /**
     * Convert an int to a byte array
     * @param value
     * @return
     */
    public static byte[] toByteArray(int value) {
        byte[] b = new byte[4];
        for (int i = 0; i < 4; i++) {
            int offset = (b.length - 1 - i) * 8;
            b[i] = (byte) ((value >>> offset) & 0xFF);
        }
        return b;
    }    
    
    /**
     * Convert a byte array to a bit string
     * @param data
     * @param leadingZeros
     * @return
     */
    public static final String toBitString(byte[] data, boolean leadingZeros) {
        if (data == null)
            return null;
        StringBuilder result = new StringBuilder(data.length*8);
        for (int i=0; i<data.length; i++) {
            byte b = data[i];
            for (int n=0; n<8; n++) {
                if ((b & 128) != 0) {
                    result.append('1');
                    leadingZeros = true;
                } else if (leadingZeros) {
                    result.append('0');
                }
                b <<= 1;
            }
        }
        if (result.length() == 0 && data.length > 0)
            return "0";
        return result.toString();
    }

    /**
     * Convert a byte array to a bit string
     * @param data
     * @return
     */
    public static final String toBitString(byte[] data) {
        return toBitString(data, true);
    }
    
    /**
     * Convert a byte array to a bit string
     * @param value
     * @return
     * @throws IllegalArgumentException
     */
    public static final String toBitString(long value) throws IllegalArgumentException {
        if (value < 0)
            throw new IllegalArgumentException("only values >= 0 can be converted to a bit string");
        if (value == 0)
            return "0";
        return toBitString(toByteArray(value), false);
    }
    
    /**
     * String all occurrences of oldText with newText by doing a case insensitive search
     * @param source - Text to search
     * @param oldText - Text to find
     * @param newText - Replace occurrences of oldText with this
     * @return
     */
    public static String replaceString(String source, String oldText, String newText) {
        return replaceString(source, oldText, newText, true, true, false);
    }

    /**
     * Returns the string representation of an int or "null" if the value is 0 (for use in database queries)
     * @param value
     * @return
     */
    public static String nullable(int value) {
        if (value > 0)
            return Integer.toString(value);
        return Constants.NULL;
    }

    /**
     * Returns a quoted string or "null" (for use in database queries)
     * @param value
     * @return
     */
    public static String nullable(String value) {
        if (value != null) {
            if (!value.trim().equals(""))
                return "'" + value.trim() + "'";
            return Constants.NULL;
        }
        return Constants.NULL;
    }

    /**
     * Convert an integer to a color (with black as the default)
     * @param colour
     * @return
     */
    public static Color convertColor(int colour) {
        return convertColor(colour, Color.black);
    }

    /**
     * Convert an integer to a color
     * @param colour
     * @param defaultColour
     * @return
     */
    public static Color convertColor(int colour, Color defaultColour) {
        if (colour != 0) {
            int c_r, c_g, c_b;

            c_b = (colour >> 16) & 0xff;
            c_g = (colour >> 8) & 0xff;
            c_r = colour & 0xff;

            return new Color(c_r, c_g, c_b);
        }
        return defaultColour;
    }

    /**
     * Convert a colour to an integer
     * @param colour
     * @return
     */
    public static int convertColor(Color colour) {
        if (colour != null) {
            int c_r, c_g, c_b;

            c_b = colour.getBlue() & 0xff;
            c_g = colour.getGreen() & 0xff;
            c_r = colour.getRed() & 0xff;

            return (c_b << 16) + (c_g << 8) + c_r;
        }
        return convertColor(Color.black);
    }

    /**
     * Prefix a folder to a filename
     * @param file
     * @param folder
     * @return
     * @model
     */
    public static String addFolder(String file, String folder) {
        if (file != null && file.indexOf(File.separatorChar) < 0 && file.indexOf(DRIVECHAR) < 0 && folder != null) {
            if (folder.lastIndexOf(File.separatorChar) == folder.length() - 1)
                return folder + file;
            return folder + File.separatorChar + file;
        } else if (file != null) {
            return file;
        } else {
            return "";
        }
    }

    /**
     * Extract a bit in a bit map as a boolean
     * @param options
     * @param option
     * @return
     */
    public static boolean checkOption(int options, int option) {
        return ((options & option) != 0);
    }

    /**
     * Close an object by streaming it to a byte array and reading it as a new object from the byte array.
     * @param source
     * @return
     */
    public static Object cloneObject(Object source) {
        if (source != null) {
            try {
                ByteArrayOutputStream data = new ByteArrayOutputStream();
                ObjectOutputStream out = new ObjectOutputStream(data);
                out.writeObject(source);
                ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data.toByteArray()));
                return in.readObject();
            } catch (ClassNotFoundException e) {
                Logger.getLogger(Utils.class).error("Toolkit.cloneObject() failed", e);
            } catch (IOException e) {
                Logger.getLogger(Utils.class).error("Toolkit.cloneObject() failed", e);
            }
        }
        return null;
    }

    /**
     * Convert a string to a color
     * @param color
     * @return
     */
    public static Color getColor(String color) {
        if (color != null) {
            if (color.toLowerCase().equals("white"))
                return Color.white;
            if (color.toLowerCase().equals("lightgray"))
                return Color.lightGray;
            if (color.toLowerCase().equals("gray"))
                return Color.gray;
            if (color.toLowerCase().equals("darkgray"))
                return Color.darkGray;
            if (color.toLowerCase().equals("black"))
                return Color.black;
            if (color.toLowerCase().equals("red"))
                return Color.red;
            if (color.toLowerCase().equals("pink"))
                return Color.pink;
            if (color.toLowerCase().equals("orange"))
                return Color.orange;
            if (color.toLowerCase().equals("yellow"))
                return Color.yellow;
            if (color.toLowerCase().equals("green"))
                return Color.green;
            if (color.toLowerCase().equals("magenta"))
                return Color.magenta;
            if (color.toLowerCase().equals("cyan"))
                return Color.cyan;
            if (color.toLowerCase().equals("blue"))
                return Color.blue;

            try {
                return Color.getColor(color);
            } catch (Throwable e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Test whether a timeout has expired
     * @param startTime
     * @param timeoutMS
     * @param expireMessage
     * @return
     */
    public static final boolean timeoutExpired(long startTime, long timeoutMS, String expireMessage) {
        if (timeoutMS < 0)
            return false;

        long now = System.currentTimeMillis();
        if (startTime != 0 && startTime + timeoutMS < now) {
            if (expireMessage != null)
                Logger.getLogger(Utils.class).info(expireMessage);
            return true;
        }
        return false;
    }

    /**
     * Format a date to a default GUI format
     * @param value
     * @return
     */
    public static String formatDate(Date value) {
        if (value != null)
            return getDefaultDateFormat().format(value);
        return "";
    }

    /**
     * Format a date and time to a default GUI format
     * @param value
     * @return
     */
    public static String formatDateTime(Date value) {
        if (value != null)
            return getDefaultTimestampFormat().format(value);
        return "";
    }

    /**
     * Normalize a two dimensional size (null if both width and height is 0)
     * @param dimension
     * @return
     */
    public static Dimension2D normalize(Dimension2D dimension) {
        if (dimension == null)
            return null;

        double x = dimension.getWidth();
        double y = dimension.getHeight();

        if (x < 0 || y < 0)
            dimension.setSize(Math.abs(x), Math.abs(y));

        return dimension;
    }

    /**
     * Normalize a rectangle (null if both width and height is 0)
     * @param rect
     * @return
     */
    public static Rectangle2D normalize(Rectangle2D rect) {
        if (rect == null)
            return null;

        double x = rect.getX();
        double y = rect.getY();
        double w = rect.getWidth();
        double h = rect.getHeight();

        if (w < 0) {
            x = x + w;
            w = -w;
            rect.setRect(x, y, w, h);
        }
        if (h < 0) {
            y = y + h;
            h = -h;
            rect.setRect(x, y, w, h);
        }
        return rect;
    }

    /**
     * Delete all null keys, all null values and optionally all 0
     * integers values
     * @param map
     * @param removeZeroInts
     * @return
     */
    public static Map<String,Object> normalize(Map<String,Object> map, boolean removeZeroInts) {
        if (map == null)
            return null;
        
        map.remove(null);
        @SuppressWarnings("unchecked")
        Map.Entry<String,Object>[] entries = map.entrySet().toArray(new Map.Entry[map.size()]);
        for (Map.Entry<String,Object> e : entries) {
            Object val = e.getValue();
            if (val == null) {
                map.remove(e.getKey());
            } else if (val instanceof CharSequence) {
                if (normalize(val.toString()) == null)
                    map.remove(e.getKey());
            } else if (val instanceof Number && removeZeroInts) {
                if (((Number) val).longValue() == 0)
                    map.remove(e.getKey());
            }
        }
        return map;
    }
    
    /**
     * Round a date (set time to 00:00:00)
     * @param value
     * @return
     * @model
     */
    public static Date round(Date value) {
    	if (value == null)
    		return null;
        return round(value, DAY_OF_MONTH);
    }

    /**
     * Round a date to a specific date part
     * @param value
     * @param datePart
     * @return
     * @model
     */
    @SuppressWarnings("fallthrough")
    public static Date round(Date value, int datePart) {
        if (value == null)
            return null;
        Calendar c = Calendar.getInstance();
        synchronized (c) {
            c.setTime(value);
            switch (datePart) {
            case YEAR:
                c.set(MONTH, JANUARY);
            case MONTH:
                c.set(DAY_OF_MONTH, 1);
            case DAY_OF_MONTH:
                c.set(HOUR_OF_DAY, 0);
            case HOUR_OF_DAY:
            case HOUR:
                c.set(MINUTE, 0);
            case MINUTE:
                c.set(SECOND, 0);
            case SECOND:
                c.set(MILLISECOND, 0);
                return c.getTime();
            default:
                throw new IllegalArgumentException("Invalid datePart");
            }
        }
    }

    /**
     * Convert any object to a normalized string
     * @param value
     * @return
     */
    public static String normalizedString(Object value) {
        if (value == null)
            return null;
        return normalize(value.toString());
    }
    
    /**
     * Normalize a string (trim all leading & trailing whitespace and return null if it is an empty string)
     * @param value
     * @return
     */
    public static String normalize(String value) {
        if (value == null)
            return null;

        value = value.trim();

        if (value.equals(""))
            return null;

        return value;
    }

    /**
     * Normalize a string (trim all leading & trailing whitespace and return null if it is an empty string)
     * @param value
     * @param defaultValue
     * @return
     */
    public static String normalizeWithDefault(String value, String defaultValue) {
        value = normalize(value);
        return (value != null ? value : defaultValue);
    }

    /**
     * Normalise an array of strings
     * @param values
     * @return
     */
    public static String[] normalize(String[] values) {
        if (values == null)
            return new String[0];

        int nulls = 0;
        for (int i=0; i<values.length; i++) {
            values[i] = normalize(values[i]);
            if (values[i] == null)
                nulls++;
        }
        if (nulls == 0)
            return values;
        
        List<String> items = new LinkedList<String>();
        for (int i = 0; i < values.length; i++) {
            if (values[i] != null)
                items.add(values[i]);
        }
        if (items.size() == 0)
            return new String[0];

        return items.toArray(new String[items.size()]);
    }

    /**
     * Split a string into substrings and normalize the resulting array
     * @param values
     * @param seperator
     * @return
     */
    public static String[] normalize(String values, String seperator) {
        if (values == null)
            return new String[0];

        if (seperator == null)
            return normalize(new String[] { normalize(values) });

        if (seperator.equals(""))
            return normalize(new String[] { normalize(values) });

        if (!seperator.equals("\n"))
            values = replaceString(values, "\n", " ");

        if (!seperator.equals("\t"))
            values = replaceString(values, "\t", " ");

        if (!seperator.equals("\r"))
            values = replaceString(values, "\r", " ");

        return normalize(split(values, seperator));
    }

    /**
     * Split a string into substrings with a maximum length
     * @param text
     * @param seperator
     * @param maxLength
     * @return
     */
    public static String[] split(String text, String seperator, int maxLength) {
        return split(text, seperator, maxLength, seperator + "+", seperator + "-");
    }

    /**
     * Split a string into substrings with a maximum length
     * @param text
     * @param seperator
     * @param maxLength
     * @param more
     * @param done
     * @return
     */
    public static String[] split(String text, String seperator, int maxLength, String more, String done) {
        if (text == null)
            return null;

        if (maxLength <= 0 || seperator == null || text.length() <= maxLength)
            return new String[] { text };

        String[] sections = split(text, seperator);
        StringBuffer line = new StringBuffer();
        LinkedList<StringBuffer> lines = new LinkedList<StringBuffer>();
        for (int i = 0; i < sections.length; i++) {
            if (line.length() > 0 && line.length() + sections[i].length() + seperator.length() + 1 >= maxLength) {
                if (more != null)
                    line.append(more);
                if (line.length() > 0) {
                    lines.add(line);
                    line = new StringBuffer();
                }
            }
            if (line.length() > 0)
                line.append(seperator);
            line.append(sections[i]);
        }
        if (done != null)
            line.append(done);

        if (line.length() > 0)
            lines.add(line);

        for (int i = 0; i < lines.size(); i++) {
            StringBuffer next = lines.get(i);
            if (next.length() > maxLength) {
                String s = next.substring(0, maxLength);
                next.delete(0, maxLength);
                lines.add(i, new StringBuffer(s));
            }
        }

        return lines.toArray(new String[lines.size()]);
    }

    /**
     * Split a string into substrings
     * @param text
     * @param seperator
     * @return
     */
    public static String[] split(String text, String seperator) {
        if (text == null)
            return null;

        LinkedList<String> lines = new LinkedList<String>();

        while (text != null) {
            int pos = text.indexOf(seperator);
            if (pos < 0)
                pos = text.length();
            lines.add(text.substring(0, pos));
            if (pos < text.length())
                text = text.substring(pos + 1);
            else
                text = null;
        }

        return lines.toArray(new String[lines.size()]);
    }

    /**
     * Turn multi-line text to a character seperated string
     * @param text
     * @param seperator
     * @return
     */
    public static String textToString(String text, String seperator) {
        if (text == null)
            return null;

        if (normalize(seperator) == null)
            seperator = ", ";

        text = replaceString(text, "\r\n", "\n");
        text = replaceString(text, "\r", "\n");

        String[] lines = split(Utils.replaceString(Utils.replaceString(text, "\r\n", "\n"), "\r", "\n"), "\n");

        StringBuffer result = new StringBuffer();
        for (int i = 0; i < lines.length; i++) {
            String line = normalize(lines[i]);
            if (line != null) {
                if (i > 0)
                    result.append(seperator);
                result.append(line);
            }
        }
        return normalize(result.toString());
    }

    /**
     * Convert multi-line text to comma seperated text
     * @param text
     * @return
     */
    public static String textToString(String text) {
        return textToString(text, ", ");
    }

    /**
     * Enclose a string in quotes
     * @param text
     * @return
     */
    public static final String quote(String text) {
        return quote(text, null, null);
    }

    /**
     * Remove quotes from quoted text
     * @param text
     * @return
     */
    public static final String unquote(String text) {
        if (normalize(text) == null)
            return text;
        if (text.length() < 2)
            return text;

        char quote = text.charAt(0);
        switch (quote) {
        case '\'':
        case '"':
            if (text.endsWith(String.valueOf(quote)))
                return text.substring(1, text.length() - 1);
        }
        return text;
    }

    /**
     * Enclose a string in quotes
     * @param text
     * @param quoteChar
     * @return
     */
    public static final String quote(String text, String quoteChar) {
        return quote(text, quoteChar, null);
    }

    /**
     * Enclose a string in quotes
     * @param text
     * @param quoteChar
     * @param quoteRepresentation
     * @return
     */
    public static final String quote(String text, String quoteChar, String quoteRepresentation) {
        if (text == null)
            return null;

        quoteChar = normalize(quoteChar);
        if (quoteChar == null)
            quoteChar = "\"";

        quoteRepresentation = normalize(quoteRepresentation);
        if (quoteRepresentation == null)
            quoteRepresentation = quoteChar + quoteChar;

        return quoteChar + replaceString(text, quoteChar, quoteRepresentation) + quoteChar;
    }

    /**
     * Round a number to a specific number of decimals (negative decimals will round to 10, 100 etc)
     * @param amount
     * @return
     */
    public static float round(float number, int decimals) {
        if (((long) number) == number || decimals == 0)
            return number;
        float factor = (float) Math.pow(10, decimals);
        return Math.round(number * factor) / factor;
    }

    /**
     * Round a number to a specific number of decimals (negative decimals will round to 10, 100 etc)
     * @param amount
     * @return
     */
    public static double round(double number, int decimals) {
        if (((long) number) == number || decimals == 0)
            return number;
        double factor = (float) Math.pow(10, decimals);
        return Math.round(number * factor) / factor;
    }

    /**
     * Prepend a URL base to a filename
     * @param file
     * @param urlBase
     * @return
     */
    public static String setURLBase(String file, String urlBase) {
        if (urlBase == null)
            return file;

        if (!urlBase.endsWith("/") && !urlBase.endsWith(File.separator))
            urlBase += "/";

        try {
            new URL(urlBase);
            return urlBase + file;
        } catch (MalformedURLException e) {
            Logger.getLogger(Utils.class).error(e);
            return file;
        }
    }

    /**
     * Set the folder of a path the path does not contain a folder seperator
     * @param file
     * @param folder
     * @return
     */
    public static String setDefaultFolder(String file, String folder) {
        if (file == null || folder == null)
            return file;

        if (file.indexOf(File.separatorChar) >= 0)
            return file;

        return folder + File.separator + file;
    }
    
    /**
     * Get a writable folder (path may start with "~" or contain argument variables)
     * @param path
     * @return
     */
    public static String getWritableFolder(String path) {
        if (Utils.normalize(path) == null) {
            path = getUserDocumentsFolder();
        } else {
            if (path.trim().startsWith("~"))
                path = path.replace("~", getUserHomeFolder());
            File dir = new File(path);
            if (!dir.mkdirs() || !dir.canWrite())
                path = getUserDocumentsFolder();
        }

        String result = path;
        if (result.trim().startsWith("~"))
            result = result.replace("~", getUserHomeFolder());
        if (!new File(result).mkdirs())
            Logger.getLogger(Utils.class).warn("Could not create folder: " + result);
        return result;
    }

    /**
     * Replace the folder in a filename
     * @param file
     * @param folder
     * @return
     */
    public static String setFileFolder(String file, String folder) {
        if (file == null)
            return null;

        int ndx = Math.max(file.lastIndexOf(File.separatorChar), file.lastIndexOf('/'));
        if (ndx >= 0)
            return setDefaultFolder(file.substring(ndx + 1), folder);
        return setDefaultFolder(file, folder);
    }

    /**
     * Return a version extension that forms part of the filename.
     * Typically used in Linux in libraries such as "libxxx.so.2".
     * @param filename
     * @return Version extension including the "." (e.g. ".2")
     */
    public static String getFileVersionExtension(String filename) {
        if (filename == null)
            return null;
        int pos = filename.lastIndexOf('.');
        String ext = filename.substring(pos+1);
        try {
            Integer.parseInt(ext);
            return filename.substring(pos);
        } catch (NumberFormatException e) {
            return null;
        }
    }
    
    /**
     * Return a version extension that forms part of the filename.
     * Typically used in Linux in libraries such as "libxxx.so.2".
     * @param filename
     * @return Version extension including the "." (e.g. ".2")
     */
    public static String removeFileVersionExtension(String filename) {
        if (filename == null)
            return null;
        String ext = getFileVersionExtension(filename);
        if (ext == null || ext.length() == 0)
            return filename;
        return filename.substring(0, filename.length()-ext.length());
    }
    
    /**
     * Return the extension of a filename.
     * @param filename
     * @return Extension including the "." (e.g. ".txt")
     */
    public static String getFileExtension(String filename) {
        String versionExt = getFileVersionExtension(filename);
        if (versionExt != null)
            filename = filename.substring(0, filename.lastIndexOf(versionExt));
        int dot = filename.lastIndexOf('.');
        int sep = filename.replaceAll("\\\\", "/").lastIndexOf('/');
        if (dot < 0 || dot < sep)
            return versionExt;
        return filename.substring(dot);
    }
    
    /**
     * Replace / add an extension of a filename
     * @param filename
     * @param extension
     * @return
     */
    public static String setExtension(String filename, String extension) {
        String versionExt = getFileVersionExtension(filename);
        if (versionExt != null)
            filename = filename.substring(0, filename.lastIndexOf(versionExt));
        else
            versionExt = "";
        
        filename = normalize(filename);
        extension = normalize(extension);

        if (filename == null || extension == null)
            return (filename != null ? filename : extension) + versionExt;

        if (!extension.startsWith("."))
            extension = "." + extension;

        int pt = filename.lastIndexOf('.');
        int sp = filename.lastIndexOf(File.separator);
        if (pt < sp || pt < 0 || (sp >= 0 && pt == sp + 1))
            return filename + extension + versionExt;
        return filename.substring(0, pt) + extension + versionExt;
    }

    /**
     * Replace / add an extension of a filename
     * @param filename
     * @param extension
     * @return
     */
    public static String setDefaultExtension(String filename, String extension) {
        filename = normalize(filename);
        extension = normalize(extension);

        if (filename == null || extension == null)
            return (filename != null ? filename : extension);

        if (!extension.startsWith("."))
            extension = "." + extension;

        int pt = filename.lastIndexOf('.');
        int sp = filename.lastIndexOf(File.separator);
        if (pt < sp || pt < 0 || (sp >= 0 && pt == sp + 1))
            return filename + extension;
        return filename;
    }

    /**
     * Execute the main method of a specific class
     * @param mainClass
     * @param args
     * @return
     */
    public static final boolean runMainMethodThread(Class<?> mainClass, String[] args) {
        return runMethodThread(mainClass, "main", null, new Class[] { String[].class }, new Object[] { args });
    }

    /**
     * Shortcut to executing a method of an object
     * @param objectClass
     * @param methodName
     * @param args
     * @return
     */
    public static final boolean runClassMethodThread(Class<?> objectClass, String methodName, Object[] args) {
        return runMethodThread(objectClass, methodName, null, null, args);
    }

    /**
     * Get argument classes from an array of objects
     * @param args
     * @return
     */
    public static final Class<?>[] getArgumentClasses(Object[] args) {
        if (args == null)
            args = new Object[0];

        Class<?>[] argClasses = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                Logger.getLogger(Utils.class).error("Argument (" + i + ") is null - could not determine class.");
                return null;
            }
            argClasses[i] = args[i].getClass();
        }

        return argClasses;
    }

    // Use this object to check argument types for which null is sent in as parameter
    public static final Object dummyObject = new Object();

    /**
     * Compare argument classes by checking sub-classing as well
     * @param argTypes
     * @param args
     * @return
     */
    public static final boolean compareArgumentTypes(Class<?>[] argTypes, Object[] args) {
        if (argTypes.length != args.length)
            return false;

        for (int i = 0; i < args.length; i++)
            if (!argTypes[i].isInstance((args[i] != null ? args[i] : dummyObject)))
                return false;

        return true;
    }

    /**
     * Select the best method to use given a set of parameters
     * @param objClass
     * @param methodName
     * @param args
     * @return
     * @throws NoSuchMethodException
     */
    public static final Method selectBestMethod(Class<?> objClass, String methodName, Object[] args) throws NoSuchMethodException {
        Method[] methods = objClass.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (!methods[i].getName().equals(methodName))
                continue;
            if (compareArgumentTypes(methods[i].getParameterTypes(), args))
                return methods[i];
        }

        throw new NoSuchMethodException();
    }

    /**
     * Sleep catching the InterruptedException
     * @param timeMS
     * @deprecated
     */
    @Deprecated
    public static final void sleep(long timeMS) {
        try {
            Thread.sleep(timeMS);
        } catch (InterruptedException e) {
            Logger.getLogger(Utils.class).info("Thread interrupted (" + Thread.currentThread() + ")");
        }
    }

    /**
     * Create a thread to run a method of an object
     * @param objClass
     * @param methodName
     * @param object
     * @param argClasses
     * @param args
     * @return
     */
    public static final boolean runMethodThread(Class<?> objClass, String methodName, Object object, Class<?>[] argClasses, Object[] args) {
        if (objClass == null) {
            Logger.getLogger(Utils.class).error("No class specified");
            return false;
        }

        if (Utils.normalize(methodName) == null) {
            Logger.getLogger(Utils.class).error("No method name specified");
            return false;
        }

        if (args == null)
            args = new Object[0];

        if (argClasses == null)
            argClasses = getArgumentClasses(args);

        Method method = null;

        try {
            if (argClasses != null)
                method = objClass.getMethod(methodName, argClasses);
            else
                method = selectBestMethod(objClass, methodName, args);
        } catch (NoSuchMethodException e) {
            Logger.getLogger(Utils.class).error(objClass.getName() + " is not a main class (must have main[] method)", e);
        }

        if (method != null)
            new MethodThread(method, object, args).start();

        return (method != null);
    }

    /**
     * Class used to run a method of an object as a seperate thread
     * @author John Bester
     */
    public static final class MethodThread extends Thread {
        private Method method;
        private Object object;
        private Object[] args;

        /**
         * Constructor
         * @param method
         */
        public MethodThread(Method method) {
            this(method, null, null);
        }

        /**
         * Constructor
         * @param method
         * @param object
         */
        public MethodThread(Method method, Object object) {
            this(method, object, null);
        }

        /**
         * Constructor
         * @param method
         * @param args
         */
        public MethodThread(Method method, Object[] args) {
            this(method, null, args);
        }

        /**
         * Constructor
         * @param method
         * @param object
         * @param args
         */
        public MethodThread(Method method, Object object, Object[] args) {
            super();
            this.method = method;
            this.object = object;
            this.args = args;
        }

        /*
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            try {
                method.invoke(object, args);
            } catch (IllegalAccessException e) {
                Logger.getLogger(Utils.class).error(e);
            } catch (InvocationTargetException e) {
                Logger.getLogger(Utils.class).error(e);
            }
        }
    }

    /**
     * Create a folder (parent folder must exist)
     * @param folder
     * @return
     */
    private static final boolean createFolder(String folder) {
        if (folder == null || folder.equals("") || folder.equals("."))
            return true;

        File dir = new File(folder);
        if (!dir.exists())
            return dir.mkdir();
        return dir.isDirectory();
    }

    /**
     * Create a folder recursively
     * @param folder
     * @return
     */
    public static final boolean buildFolder(String folder) {
        if (folder == null)
            return false;
        if (folder.equals("") || folder.equals(".") || folder.equals(File.separator))
            return true;
        if (folder.equals(".."))
            return false;

        String sep = File.separator;
        if (sep.equals("\\"))
            sep = "\\\\";

        String[] parts = folder.split(sep);

        String fld = "";
        for (int i = 0; i < parts.length; i++) {
            fld += parts[i];
            if (!createFolder(fld))
                return false;
            fld += File.separator;
        }
        return true;
    }

    /**
     * Get the current user's home folder
     * @return
     */
    public static final String getUserHomeFolder() {
        try {
            return System.getProperty("user.home");
        } catch (AccessControlException e) {
            return ".";
        }
    }

    /**
     * Get the current user's documents folder
     * @return
     */
    public static final String getUserDocumentsFolder() {
        try {
            String tmp = getUserHomeFolder();
            if (tmp == null)
                return ".";
            File home = new File(tmp);
            File result = new File(home, "Documents");
            return (result.isDirectory() ? result : home).getAbsolutePath();
        } catch (AccessControlException e) {
            return ".";
        }
    }

    /**
     * Get the current user's username
     * @return
     */
    public static final String getUserName() {
        return System.getProperty("user.name");
    }

    /**
     * Get the language setting of the current user
     * @return
     */
    public static final String getUserLanguage() {
        return System.getProperty("user.language");
    }

    /**
     * Get the current folder (only available on some operating systems)
     * @return
     */
    public static final String getCurrentFolder() {
        try {
            return System.getProperty("user.dir");
        } catch (AccessControlException e) {
            return ".";
        }
    }
    
    public static String getApplicationFolder() {
        Class<?> mainClass = Arguments.getMainClass();
        if (mainClass == null)
        	return ".";
        String resPath = "/" + mainClass.getName().replace('.', '/') + ".class";
        URL url = Utils.class.getResource(resPath);
        
        String host = Utils.normalize(url.getHost());
        if (host != null && !host.equals("."))
            return getCurrentFolder();
        
        String proto = url.getProtocol();
        if (proto == null)
            return getCurrentFolder();

        String path = url.getPath().replace('\\', '/');
        int ndx = path.lastIndexOf('!');
        if (ndx >= 0)
            path = path.substring(0, ndx); 
        
        if (proto.equals("jar")) {
            try {
                url = new URL(path);
                path = url.getPath();
                proto = url.getProtocol();
                if (proto != null && !proto.equalsIgnoreCase("file"))
                    return getCurrentFolder();
            } catch (MalformedURLException e) {
                // Ignore exception, path is probably not a URI (as it would be in the case of a jar)
            }
        } else if (proto.equals("file")) {
            int res = path.indexOf(resPath);
            if (res > 0)
                path = path.substring(0, res);
        } else {
            return getCurrentFolder();
        }
        
        File file = new File(path);
        if (!file.exists())
            return getCurrentFolder();
        
        if (file.isFile()) 
            file = file.getParentFile();
        
        if (file == null)
            return getCurrentFolder();
        
        if (file.getName().equalsIgnoreCase("bin") || file.getName().equalsIgnoreCase("lib"))
            file = file.getParentFile();
        
        if (file == null)
            return getCurrentFolder();
        
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            return file.getAbsolutePath();
        }
    }
    
    /**
     * Copy a byte buffer catching index out of bounds exception
     * @param source
     * @param srcNdx
     * @param target
     * @param tgtNdx
     * @param length
     */
    public static final void copy(byte[] source, int srcNdx, byte[] target, int tgtNdx, int length) {
        try {
            System.arraycopy(source, srcNdx, target, tgtNdx, length);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    /**
     * Copy the contents of an InputStream to an OutputStream 
     * @param in
     * @param out
     * @throws IOException
     */
    public static void copy(InputStream in, OutputStream out) throws IOException {
        byte[] buf = new byte[10240];
        int read = 0;
        while ((read = in.read(buf)) > 0)
            out.write(buf, 0, read);
        out.flush();
    }
    
    /**
     * Copy the contents of a Reader to a Writer
     * @param in
     * @param out
     * @throws IOException
     */
    public static void copy(Reader in, Writer out) throws IOException {
        char[] buf = new char[10240];
        int read = 0;
        while ((read = in.read(buf)) > 0)
            out.write(buf, 0, read);
        out.flush();
    }
    
    private static void copyFile(File source, File target, boolean overwrite) throws IOException {
    	InputStream in = new BufferedInputStream(new FileInputStream(source));
    	try {
    		if (!overwrite && target.exists())
    			throw new IOException("Target file already exists");
        	OutputStream out = new BufferedOutputStream(new FileOutputStream(target));
        	try {
        	    copy(in, out);
        	} finally {
        		out.close();
        	}
    	} finally {
    		in.close();
    	}
    }
    
    public static void copy(File source, File target, boolean overwrite) throws IOException {
    	if (source == null || target == null)
    		throw new IllegalArgumentException("Source and target must be specified");
    	if (source.isDirectory() && target.exists() && !target.isDirectory())
    		throw new IllegalArgumentException("If source is a folder, then target must calso be a folder");
    	if (source.isFile() && target.isDirectory())
    		target = new File(target, source.getName());
    	if (source.isDirectory() && !target.exists()) {
    		if (!target.mkdirs())
    			throw new IOException("Could not create target folder: " + target);
    	}
    		
    	if (source.isFile()) {
    		copyFile(source, target, overwrite);
    	} else if (source.isDirectory()) {
    		for (File f : source.listFiles()) {
    			if (f.isDirectory()) {
    				copy(f, new File(target, f.getName()), overwrite);
    			} else {
    				copyFile(f, target, overwrite);
    			}
    		}
    	} else {
    		throw new IOException("Source file not found or invalid");
    	}
    }
    
    /**
     * Set the default font for all swing components
     * @param font
     * @param style
     * @param size
     */
    public static void setDefaultFont(String font, int style, int size) {
        setDefaultFont(new javax.swing.plaf.FontUIResource(font, style, size));
    }

    /**
     * Sets the default font for all Swing components. e.g. setDefaultFont (new javax.swing.plaf.FontUIResource("Serif",Font.ITALIC,12));
     * @param f
     */
    public static void setDefaultFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration<?> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, f);
        }
    }

    /**
     * Build a class definition of a class as comma seperated text
     * @param en
     * @return
     */
    public static String toString(Class<?> en) {
        try {
            Method values = en.getMethod("values", (Class[]) null);
            if (values == null)
                return en.getName();

            Object returned = values.invoke(en, (Object[]) null); // new Object[0]
            if (returned instanceof Object[]) {
                StringBuffer result = new StringBuffer();

                for (Object o : (Object[]) returned)
                    if (o instanceof Enum<?>)
                        result.append(((Enum<?>) o).name() + ",");
                    else
                        result.append(o + ",");

                if (result.length() > 0)
                    result.deleteCharAt(result.length() - 1);

                return result.toString();
            }
            return null;
        } catch (SecurityException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Build a class definition of a class as an array of strings
     * @param e
     * @return
     */
    public static String[] toStringArray(Class<?> e) {
        String result = toString(e);
        if (result == null)
            return null;
        return result.split(",");
    }
    
    /**
     * Create a shortened version of a class name by using only the 
     * first character of each of the package name sections.
     * @param object
     */
    public static String getShortClassName(Object object) {
        if (object == null)
            return "NULL";
        String[] parts = object.getClass().getName().split("\\.");
        switch (parts.length) {
        case 0 :
            return "NULL";
        case 1 :
            return parts[0];
        default :
            StringBuffer result = new StringBuffer();
            for (int i=0; i<parts.length - 1; i++) {
                result.append(parts[i].substring(0,1));
                result.append(".");
            }
            result.append(parts[parts.length-1]);
            return result.toString();
        }
    }
    
    /**
     * Find the first available error message by scanning chain of exceptions
     * @param e
     * @return
     */
    public static String getMessage(Throwable e) {
        while (e != null && e.getMessage() == null) {
            if (e instanceof SQLException && ((SQLException) e).getNextException() != null)
                e = ((SQLException) e).getNextException();
            else
                e = e.getCause();
        }
        return (e != null ? e.getMessage() : null);    
    }

    /**
     * Encode a path as a URL
     * @param path
     * @return
     * @throws UnsupportedEncodingException 
     * @throws UnsupportedEncodingException
     */
    public static String encodeUrlPath(String path) throws UnsupportedEncodingException {
        if (path == null || path.length() == 0)
            return "";
        StringBuilder result = new StringBuilder();
        if (path.startsWith("/"))
            result.append('/');
        String[] parts = path.split("/");
        for (String part : parts) {
            if (result.length() > 0 && result.charAt(result.length() - 1) != '/')
                result.append('/');
            if (!part.equals("."))
                result.append(URLEncoder.encode(part, "UTF-8").replaceAll("\\+", "%20"));
        }
        return result.toString();
    }

    /**
     * Re-throw an exception as a different exception class
     * @param <T>
     * @param error
     * @param clazz
     */
    public static <T extends Throwable> T cast(Throwable error, Class<T> clazz) {
    	return cast(error, clazz, null);
    }
    
    /**
     * Re-throw an exception as a different exception class
     * @param <T>
     * @param error
     * @param clazz
     * @param message
     */
    public static <T extends Throwable> T cast(Throwable error, Class<T> clazz, String message) {
    	String tmp = error.getMessage();
    	if (tmp == null)
    		tmp = "";
        if (clazz.isInstance(error) && (message == null || message.equals(tmp))) {
            @SuppressWarnings("unchecked")
            T result = (T) error;
            return result;
        }
        if (message == null || message.length() == 0)
        	message = error.getMessage();
        if (clazz == ParseException.class) {
            @SuppressWarnings("unchecked")
            T result = (T) new ParseException(message, 0);
            result.setStackTrace(error.getStackTrace());
            return result;
        }
        T exception = null;
        int state = 0;
        while (state < 5 && exception == null) {
            try {
                switch (state) {
                case 0 :
                    exception = clazz.getConstructor(String.class, Throwable.class).newInstance(message, error);
                    break;
                case 1 :
                    exception = clazz.getConstructor(Throwable.class, String.class).newInstance(error, message);
                    break;
                case 2 :
                    exception = clazz.getConstructor(Throwable.class).newInstance(error);
                    break;
                case 3 :
                    exception = clazz.getConstructor(String.class).newInstance(message);
                    break;
                default :
                    exception = clazz.getConstructor().newInstance();
                    exception.initCause(error);
                }
            } catch (Exception e) {
                // Ignore exception
            } finally {
                state++;
            }
        }
        if (exception == null) {
            ClassCastException cce = new ClassCastException("Could not instantiate an exception of class " + clazz.getName());
            cce.initCause(error);
            throw cce;
        }
        exception.setStackTrace(error.getStackTrace());
        return exception;
    }

    /**
     * Get the main jar manifest
     * @return
     * @throws IOException
     */
    public static Manifest getManifest() throws IOException {
        Class<?> c = Arguments.getMainClass(); 
        if (c == null)
            c = Arguments.class;
        
        return getManifestByClass(c);
    }
    
    /**
     * Get a jar manifest by specifying a class contained in the jar
     * @return
     * @throws IOException
     */
    public static Manifest getManifestByClass(Class<?> somePackagedClass) throws IOException {
        if (somePackagedClass == null)
            return null;
        URL jarURL = Utils.class.getResource("/" + somePackagedClass.getName().replaceAll("\\.", "/") + ".class");
        try {
            JarURLConnection jurlConn = (JarURLConnection) jarURL.openConnection();
            return jurlConn.getManifest();
        } catch (ClassCastException e) {
            throw new IOException("Manifest only available when starting from JAR");
        }
    }
    
    /**
     * Get a jar file by looking up a class
     * @return
     * @throws IOException
     */
    public static JarFile getJarFile(Class<?> somePackagedClass) throws IOException {
        if (somePackagedClass == null)
            return null;
        URL jarURL = Utils.class.getResource("/" + somePackagedClass.getName().replaceAll("\\.", "/") + ".class");
        try {
            JarURLConnection jurlConn = (JarURLConnection) jarURL.openConnection();
            JarFile result = jurlConn.getJarFile();
            if (result != null)
                return result;
            throw new IOException("Class not part of a JAR file");
        } catch (ClassCastException e) {
            throw new IOException("Manifest only available when starting from JAR");
        }
    }
    
    /**
     * Return a manifest by specifying a path to the jar file
     * @param jarPath
     * @return
     * @throws IOException
     */
    public static Manifest getManifestByJar(String jarPath) throws IOException {
        JarFile jf = new JarFile(jarPath);
        try {
        	return jf.getManifest();
        } finally {
        	jf.close();
        }
    }
    
    /**
     * Return true if a manifest can be found for the application
     * @return
     */
    public static boolean hasManifest() {
        try {
            return (getManifest() != null);
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Get a map of manifest attributes
     * @throws IOException 
     */
    public static Map<String,Attributes> getManifestAttributes() throws IOException {
        return getManifest().getEntries();
    }     
    
    /**
     * Get the version number of the application from the main jar manifest
     * @return
     */
    public static String getVersion() {
        try {
            return getVersion(getManifest());
        } catch (IOException e1) {
            if (getVersionErrorCount++ < 1)
                System.err.println(e1.getMessage());
        }
        return DEFAULT_VERSION;
    }

    /**
     * Get the version number of the application from the main jar manifest
     * @param manifest
     * @return
     */
    public static String getVersion(Manifest manifest, String defaultVersion) {
        if (manifest == null)
            return defaultVersion;
        Attributes attr = manifest.getMainAttributes();
        String result = Utils.normalize(attr.getValue("Bundle-Version"));
        if (result == null)
            result = Utils.normalize(attr.getValue("Implementation-Version"));
        if (result == null)
            result = Utils.normalize(attr.getValue("Specification-Version"));
        if (result != null)
            return result;
        return defaultVersion;
    }

    /**
     * Get the version number of the application from the main jar manifest
     * @param manifest
     * @return
     */
    public static String getVersion(Manifest manifest) {
        return getVersion(manifest, DEFAULT_VERSION);
    }

    public static File[] getAllJarFiles() {
        List<File> result = new LinkedList<File>();
        String classPath = System.getProperty("java.class.path");
        if (classPath != null) {
            String[] parts = classPath.split(Pattern.quote(File.pathSeparator));
            for (String part : parts) {
                String ext = getFileExtension(part);
                if (ext == null)
                    continue;
                if (ext.startsWith("."))
                    ext = ext.substring(1);
                if (!ext.equalsIgnoreCase("jar") && !ext.equalsIgnoreCase("zip"))
                    continue;
                File f = new File(part);
                if (f.isFile())
                    result.add(f);
            }
        }
        String extDirs = System.getProperty("java.ext.dirs");
        if (extDirs != null) {
            String[] parts = extDirs.split(Pattern.quote(File.pathSeparator));
            for (String part : parts) {
                File dir = new File(part);
                if (dir.isDirectory()) {
                    for (File f : dir.listFiles()) {
                        if (!f.isFile())
                            continue;
                        String ext = getFileExtension(f.getName());
                        if (ext == null)
                            continue;
                        if (ext.startsWith("."))
                            ext = ext.substring(1);
                        if (!ext.equalsIgnoreCase("jar") && !ext.equalsIgnoreCase("zip"))
                            continue;
                        result.add(f);
                    }
                }
            }
        }
        return result.toArray(new File[result.size()]);
    }

    /**
     * Return the most descriptive Jar or bundle name 
     * @param manifest
     * @return
     */
    public static String getJarName(Manifest manifest) {
        if (manifest == null)
            return null;
        Attributes attr = manifest.getMainAttributes();
        String result = normalize(attr.getValue("Bundle-Name"));
        if (result != null)
            return result;
        result = normalize(attr.getValue("Bundle-SymbolicName"));
        if (result != null)
            return result;
        result = normalize(attr.getValue("Specification-Title"));
        if (result != null)
            return result;
        return normalize(attr.getValue("Extension-Name"));
    }
    
    /**
     * Get the most descriptive name for a jar file
     * @param jar
     * @return
     */
    public static String getJarName(JarFile jar) {
        try {
            String result = getJarName(jar.getManifest());
            if (result != null)
                return result;
        } catch (IOException e) {
            Logger.getLogger(Utils.class).error("Could not get manifest from " + jar.getName());
        }
        String result = normalize(removeFileVersionExtension(new File(jar.getName()).getName()));
        return (result != null ? result : "unknown");
    }

    /**
     * Return an application name by looking at the manifest of the Jar from which 
     * the main(String[]) method was called.
     * @return
     */
    public static String getApplicationName() {
        try {
            String result = normalize(getJarName(getManifestByClass(Arguments.getMainClass())));
            if (result != null)
                return result;
        } catch (IOException e) {
            Logger.getLogger(Utils.class).error("Could not read application manifest", e);
        }
        return "Unknown";
    }
    
    /**
     * Return an application description (name and version) by looking at the manifest of 
     * the Jar from which the main(String[]) method was called.
     * @param defaultDescription
     * @return
     */
    public static String getApplicationDescription(String defaultDescription) {
        try {
            Manifest mf =  getManifestByClass(Arguments.getMainClass());
            if (mf == null)
                return null;
            String name = normalize(getJarName(mf));
            String version = normalize(getVersion(mf, null));
            if (name == null)
                name = defaultDescription;
            if (name == null)
                return null;
            if (version != null)
                return name + " version " + version;
            return name;
        } catch (IOException e) {
            Logger.getLogger(Utils.class).error("Could not read application manifest", e);
        }
        return defaultDescription;
    }
    
    /**
     * Return an application description (name and version) by looking at the manifest of 
     * the Jar from which the main(String[]) method was called.
     * @return
     */
    public static String getApplicationDescription() {
        return getApplicationDescription("Unknown");
    }
    
    /**
     * Return the versions of all the jar files in the class path and in the java.ext.dirs path
     * @return
     */
    public static Map<String,String> getAllJarVersions() {
        Map<String,String> result = new LinkedHashMap<String,String>();
        for (File f : getAllJarFiles()) {
            try {
                JarFile jar = new JarFile(f);
                String name = getJarName(jar);
                String version = getVersion(jar.getManifest(), null);
                if (name != null && version != null)
                    result.put(name, version);
            } catch (IOException e) {
                Logger.getLogger(Utils.class).error("Error opening JAR file " + f.getAbsolutePath());
            }
        }
        return result;
    }
    
    /**
     * Return a description of the Java VM 
     * @return
     */
    public static String getJavaVmDescription() {
        return System.getProperty("java.runtime.name")
            + " version "
            + System.getProperty("java.version")
            + "  ( "
            + System.getProperty("java.vm.vendor")
            + " )";
    }
    
    /**
     * Find the most likely address that this PC is referred to on a LAN on a specific 
     * network interface.
     * @param nic
     * @return
     */
    private static String findPreferredInetAddress(NetworkInterface nic) {
        String alternativeV6 = null;
        String alternative = null;
        Enumeration<InetAddress> addresses = nic.getInetAddresses();
        while (addresses.hasMoreElements()) {
            InetAddress adr = addresses.nextElement();
            if (adr instanceof Inet4Address)
                return adr.getHostAddress();
            if (adr instanceof Inet6Address)
                alternativeV6 = adr.getHostAddress();
            else
                alternative = adr.getHostAddress();
        }
        return (alternativeV6 != null ? alternativeV6 : alternative);
    }

    /**
     * Find the most likely address that this PC is referred to on a LAN on a specific 
     * network interface.
     * @return
     */
    private static String findPreferredInetAddress() {
        String local = null;
        String virt = null;
        String p2p = null;
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface nic = interfaces.nextElement();
                try {
                    if (!nic.isUp())
                        continue;
                    if (nic.isLoopback()) {
                        if (local == null)
                            local = findPreferredInetAddress(nic);
                    } else if (!nic.isVirtual()) {
                        if (virt == null)
                            virt = findPreferredInetAddress(nic);
                    } else if (!nic.isPointToPoint()) {
                        if (p2p == null)
                            p2p = findPreferredInetAddress(nic);
                    } else {
                        return findPreferredInetAddress(nic);
                    }
                } catch (SocketException e) {
                    continue;
                }
            }
            if (p2p != null)
                return p2p;
            if (local != null)
                return local;
            return (virt != null ? virt : "127.0.0.1");
        } catch (SocketException e) {
            return "127.0.0.1";
        }
    }

    /**
     * Return the most likely address that this PC is referred to on a LAN
     * @return
     */
    public static String getPreferredInetAddress() {
        if (prefferredInetAddress == null)
            prefferredInetAddress = findPreferredInetAddress();
        return prefferredInetAddress;
    }

    /**
     * Perform a deep comparison the contents of two objects
     * @param src
     * @param tgt
     * @return
     */
    public static boolean deepCompareObjects(Object src, Object tgt) {
        if (src == null || tgt == null)
            return (src == tgt);
        if (src instanceof Map<?,?> && src instanceof Map<?,?>) {
            return deepCompareMap((Map<?,?>) src, (Map<?,?>) tgt);
        } else if (src instanceof Collection<?> && tgt instanceof Collection<?>) {
            if (!deepCompareCollections((Collection<?>) src, (Collection<?>) tgt))
                return false;
        } else if (src.getClass().isArray() && tgt.getClass().isArray()) {
            int len = Array.getLength(src);
            if (len != Array.getLength(tgt))
                return false;
            for (int i=0; i<len; i++) {
                if (!deepCompareObjects(Array.get(src, i), Array.get(tgt, i)))
                    return false;
            }
        } 
        return src.equals(tgt);
    }
    
    /**
     * Compare the items in a list
     * @param src
     * @param tgt
     * @return
     */
    public static boolean deepCompareLists(List<?> src, List<?> tgt) {
        if (src == null || tgt == null)
            return (src == tgt);
        if (src.size() != tgt.size())
            return false;
        Iterator<?> siter = src.iterator();
        Iterator<?> titer = tgt.iterator();
        while (siter.hasNext()) {
            Object s = siter.next();
            Object t = titer.next();
            if (!deepCompareObjects(s, t))
                return false;
        }
        return true;
    }
    
    /**
     * Compare the entries in two sets
     * @param src
     * @param tgt
     * @return
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static boolean compareSets(Set<?> src, Set<?> tgt) {
        if (src == null || tgt == null)
            return (src == tgt);
        
        if (src.size() != tgt.size())
            return false;

        Set<?> tmp = new HashSet<Object>();
        tmp.addAll((Collection) src);
        tmp.removeAll(tgt);
        return (tmp.size() == 0);
    }

    /**
     * Perform a deep compare of two collections
     * @param src
     * @param tgt
     * @return
     */
    public static boolean deepCompareCollections(Collection<?> src, Collection<?> tgt) {
        if (src instanceof List<?> && tgt instanceof List<?>)
            return deepCompareLists((List<?>) src, (List<?>) tgt);
        
        if (src == null || tgt == null)
            return (src == tgt);

        if (src instanceof Set<?> && tgt instanceof Set<?>)
            return compareSets((Set<?>) src, (Set<?>) tgt);
        
        if (src.size() != tgt.size())
            return false;
        
        Iterator<?> siter = src.iterator();
        Iterator<?> titer = tgt.iterator();
        while (siter.hasNext()) {
            Object s = siter.next();
            Object t = titer.next();
            if (!deepCompareObjects(s, t))
                return false;
        }
        return true;
    }
    
    /**
     * Do a deep compare of the contents of two maps
     * @param src
     * @param tgt
     * @return
     */
    public static boolean deepCompareMap(Map<?,?> src, Map<?,?> tgt) {
        if (src == null || tgt == null)
            return (src == tgt);
        if (src.size() != tgt.size())
            return false;
        for (Map.Entry<?,?> e : src.entrySet()) {
            Object sval = e.getValue();
            Object tval = tgt.get(e.getKey());
            if (!deepCompareObjects(sval, tval))
                return false;
        }
        return true;
    }

    /**
     * Compare the contents of two maps
     * @param src
     * @param tgt
     * @return
     */
    public static boolean compareMap(Map<?,?> src, Map<?,?> tgt) {
        if (src == null || tgt == null)
            return (src == tgt);
        if (src.size() != tgt.size())
            return false;
        for (Map.Entry<?,?> e : src.entrySet()) {
            Object sval = e.getValue();
            Object tval = tgt.get(e.getKey());
            if (sval == null || tval == null) {
                if (sval != tval)
                    return false;
                continue;
            }
            if (!sval.equals(tval))
                return false;
        }
        return true;
    }

    /**
     * Convert an object to a string
     * @param value
     * @return
     */
    private static String toString(Object value) {
        if (value != null)
            return Utils.normalize(value.toString());
        return null;
    }
    
    /**
     * Get the IP address if this is an LTSP or SSH terminal
     * @return
     */
    private static String getLinuxTerminalKey() {
        Map<String,Object> env = PropertyMap.getPropertyMap(System.getenv());
        String result = toString(env.get("LTSP_CLIENT"));
        if (result == null)
            result = toString(env.get("SSH_CLIENT"));
        if (result == null)
            result = toString(env.get("SSH_CONNECTION"));
        if (result == null)
            result = toString(env.get("ESPEAKER"));
        
        System.out.println("Terminal: " + result);
        
        if (result == null) 
            return null;

        String[] tmp = Utils.normalize(result.replace('\t', ' ').replace(':', ' ').split(" "));
        if (tmp != null && tmp.length > 0) {
            for (String ip : tmp) {
                try {
                    System.out.println("Trying IP " + ip);
                    result = Utils.toHexString(InetAddress.getByName(ip).getAddress());
                } catch (Exception e) {
                    // Ignore exception
                }
            }
        }
        return result;
    }

    /**
     * Get the IP address if this is an LTSP or SSH terminal
     * @return
     */
    public static String getTerminalKey() {
        switch (Constants.OPERATING_SYSTEM) {
        case OS_LINUX :
        case OS_UNIX :
        case OS_MAC :
            return getLinuxTerminalKey();
        case OS_WINDOWS :
            return null;
        default :
            return null;
        }
    }
    
    /**
     * Return an ID that can be used to identify this machine / terminal uniquely
     * @param hostAddress
     * @return
     * @throws IllegalStateException
     */
    public static String getStationID(String hostAddress) throws IllegalStateException {
        String svrname = Utils.normalize(hostAddress);
        String hostAsBits = null;
        try {
            InetAddress svr = (svrname != null ? InetAddress.getByName(svrname) : InetAddress.getLocalHost());
            hostAsBits = Utils.toBitString(svr.getAddress());
        } catch (UnknownHostException e) {
            // Ignore exception
        }
        
        String terminal = getTerminalKey();
        if (terminal != null) {
            terminal = "/" + terminal;
            System.out.println("Adding terminal address to key");
        } else {
            terminal = "";
        }
        
        try {
            List<String> macs = new LinkedList<String>();
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces(); 
            while (nets.hasMoreElements()) {
                NetworkInterface iface = nets.nextElement();
                for (InterfaceAddress ia : iface.getInterfaceAddresses()) {
                    if (!(ia.getAddress() instanceof Inet4Address))
                        continue;
                    int maskLen = ia.getNetworkPrefixLength();
                    String mask = Utils.toBitString(ia.getAddress().getAddress()).substring(0, maskLen);
                    if (hostAsBits != null && hostAsBits.length() >= maskLen && maskLen >= 8 && hostAsBits.subSequence(0, maskLen).equals(mask)) {
                        System.out.println("Using MAC of " + iface.getName() + " as identifier");
                        return Utils.toHexString(iface.getHardwareAddress()) + terminal;
                    }
                }
                if (iface.isLoopback() || iface.isVirtual() || iface.isPointToPoint())
                    continue;
                if (!iface.isUp())
                    continue;
                byte[] mac = iface.getHardwareAddress();
                if (mac == null || mac.length == 0)
                    continue;
                macs.add(Utils.toHexString(mac));
            }
            Collections.sort(macs);
            if (macs.size() == 0) {
                System.out.println("Using keyword as identifier");
                return "localhost" + terminal;
            }
            if (macs.size() == 1)
                return macs.get(0);
            StringBuilder result = new StringBuilder();
            for (String mac : macs)
                result.append(mac);
            System.out.println("Using multiple MAC as identifier");
            if (result.length() > 20)
                return result.toString().substring(0, 20) + terminal;
            return result.toString() + terminal;
        } catch (SocketException e) {
            throw Utils.cast(e, IllegalStateException.class);
        }
    }
    
    public static String checksum(byte[] data, String algorithm) throws NoSuchAlgorithmException {
        if (data == null)
            return null;
        MessageDigest cs = MessageDigest.getInstance(algorithm);
        cs.reset();
        if (data.length > 0)
            cs.update(data);
        return toHexString(cs.digest());
    }

    public static String checksum(String text, String algorithm) throws NoSuchAlgorithmException {
        return checksum((text != null ? text.getBytes() : null), algorithm);
    }

    public static String md5(String text) throws NoSuchAlgorithmException {
        return checksum((text != null ? text.getBytes() : null), "MD5");
    }
}

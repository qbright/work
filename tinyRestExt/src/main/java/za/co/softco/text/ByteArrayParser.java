/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: ByteArrayParser.java,v $
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
 *  Revision 1.4  2006/03/29 07:13:37  goofyxp
 *  *** empty log message ***
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

import za.co.softco.util.Utils;

/**
 * Parser to handle byte array objects
 * @author John Bester
 * @model
 */
public class ByteArrayParser implements Parser<byte[]> {

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
    public byte[] parse(String value) throws ParseException {
        if (value == null)
            return null;
        byte[] buffer = new byte[(value.length() / 2) + 1];
        int len = parseByteBuffer(value, buffer, 0);
        return Utils.copy(buffer, 0, len);
    }

    /**
     * Parse a string into an array of bytes
     * @param values - Various sections of text that should be parsed differently
     * @param buffer - Buffer to write results to
     * @param offset - The offset in the buffer where data must be written to
     * @return The number of bytes added to the buffer
     * @throws ParseException
     * @model
     */
    public static final int parseByteBuffer(String[] values, byte[] buffer, int offset) throws ParseException {
        int len = 0;
        for (int i = 0; i < values.length; i++)
            len += parseByteBuffer(values[i], buffer, offset + len);
        return len;
    }

    /**
     * Parse a string into an array of bytes. This function can be recursively called when the string is built up of different sections that are
     * formatted differently. In this case parseByteBuffer(String[]...) is called which in turn calls this function for each string value.
     * @param value - A single string that must be parsed.
     * @param buffer - Buffer to write results to
     * @param offset - The offset in the buffer where data must be written to
     * @return The number of bytes added to the buffer
     * @throws ParseException
     * @model
     */
    public static final int parseByteBuffer(String value, byte[] buffer, int offset) throws ParseException {
        if (value == null || buffer == null)
            return 0;
        value = value.trim().toUpperCase();

        if (value.indexOf(',') >= 0)
            return parseByteBuffer(Utils.split(value, ","), buffer, offset);

        boolean hex = value.startsWith("0X");
        if (hex)
            value = value.substring(2);
        int len = 0;
        int digits = 0;
        int val = 0;
        for (int i = 0; i < value.length(); i++) {
            if (offset + len >= buffer.length)
                throw new ParseException("Buffer overflow", offset + len);
            char ch = value.charAt(i);
            switch (ch) {
            case ' ':
                if (digits > 0) {
                    buffer[offset + len] = (byte) val;
                    len++;
                    val = 0;
                    digits = 0;
                }
                break;
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                if (hex) {
                    if (digits == 2) {
                        buffer[offset + len] = (byte) val;
                        len++;
                        val = ch - '0';
                        digits = 1;
                    } else {
                        val = (val * 16) + ch - '0';
                        digits++;
                    }
                } else {
                    if (digits == 3) {
                        buffer[offset + len] = (byte) val;
                        len++;
                        val = ch - '0';
                        digits = 1;
                    } else {
                        val = (val * 10) + ch - '0';
                        digits++;
                    }
                }
                break;
            case 'A':
            case 'B':
            case 'C':
            case 'D':
            case 'E':
            case 'F':
                if (hex) {
                    if (digits == 2) {
                        buffer[offset + len] = (byte) val;
                        len++;
                        val = 10 + ch - 'A';
                        digits = 1;
                    } else {
                        val = (val * 16) + 10 + ch - 'A';
                        digits++;
                    }
                } else {
                    throw new ParseException("Invalid decimal character '" + ch + "'", offset + len);
                }
                break;
            default:
                throw new ParseException("Invalid character '" + ch + "'", offset + len);
            }
        }
        if (digits > 0) {
            if (offset + len >= buffer.length)
                throw new ParseException("Buffer overflow", offset + len);
            buffer[offset + len] = (byte) val;
            len++;
        }
        return len;
    }

    /*
     * @see za.co.softco.parser.Parser#toString(java.lang.Object)
     */
    @Override
    public String toString(Object value) {
        return Utils.toHexString((byte[]) (value instanceof byte[] ? value : null));
    }

    /**
     * Parse a string into an array of bytes
     * @param data
     * @return
     * @throws ParseException
     * @model
     */
    public static byte[] parseByteBuffer(String data) throws ParseException {
        return DataParser.parse(data, byte[].class);
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
    public byte[] cast(Object value) {
        return byte[].class.cast(value);
    }
}
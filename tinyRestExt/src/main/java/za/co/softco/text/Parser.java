/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: Parser.java,v $
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
 *  Revision 1.3  2006/03/18 19:53:25  obelix
 *  Restructure code to use Map<String, Object> in stead of Properties
 *
 *  Revision 1.2  2006/03/10 07:44:08  goofyxp
 *  Added generics
 *
 *  Revision 1.1  2006/01/10 14:58:47  goofyxp
 *  Renamed za.co.softco.parser package to za.co.softco.text
 *
 *  Revision 1.2  2005/12/05 09:25:10  obelix
 *  Add comments
 *  Add new format(Object, String) function
 *
 *  Created on 01-Dec-2005
 *******************************************************************************/
package za.co.softco.text;

import java.text.ParseException;

/**
 * Implement this interface for a specific class of object and register it in DataParser class
 * @author John Bester
 * @model
 */
public interface Parser<Type> {

    /**
     * If this function returns true, the DataParser class will do some preliminary casting. For example: If an array or collection should be casted
     * to a singular object, then the DataParser class will use the first not-null component of the array or collection as parameter to the specific
     * parser. Similarly, if a single object is passed in and an array or collection is required, then the DataParser class will convert the single
     * object to an array with one element before passing it on to the specific parser.
     * @return
     */
    public boolean allowMultiItemPrecast();

    /**
     * Parse an object from a string. When null is passed in, null must be returned
     * @param value - String to be parsed
     * @return The object representing the value of the string
     * @throws ParseException
     * @model
     */
    public Type parse(String value) throws ParseException;

    /**
     * Implement this method to format a value based on a format in string format. For example, a date can be formatted with "dd-mm-yyyy" as format.
     * @param value - Value to be formatted
     * @param format - Format describing how value should be formatted
     * @return The formatted value
     * @throws A ParseException should be thrown if the format is invalid.
     * @model
     */
    public String format(Object value, String format) throws ParseException;

    /**
     * Return a default string representation of the value
     * @param value - Value to be converted to a string
     * @return The default string representation of value parameter
     * @model
     */
    public String toString(Object value);

    /**
     * Convert an object to type Type
     * @param value
     * @return
     * @model
     */
    public Type cast(Object value);
}

/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 27 Jan 2011
 *******************************************************************************/
package za.co.softco.text.xml.parse;

import java.text.ParseException;

/**
 * Implement this interface to convert attribute values to and from XML text
 * @author john
 */
public interface AttributeParser<T> {

    /**
     * Parse text as a specific type of object
     * @param value
     * @return
     * @throws ParseException
     */
    public T parse(String value) throws ParseException;
    
    /**
     * Cast an object to text
     * @param value
     * @return
     */
    public String toString(T value);
}

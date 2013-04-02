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
import java.util.Map;

/**
 * Return an instance of AttributeParser for a specific type of object
 * @author john
 */
public interface AttributeParserFactory {

    /**
     * Return a parser for a specific type of value
     * @param <T>
     * @param type
     * @return
     * @throws ParseException
     */
    public <T> AttributeParser<T> getParser(Class<T> type) throws ParseException;
    
    /**
     * Parse a value to a specific type
     * @param <T>
     * @param value
     * @param type
     * @return
     * @throws ParseException
     */
    public <T> T parse(String value, Class<T> type) throws ParseException;
    
    /**
     * Parse all attributes and set them on a bean object
     * @param attribs
     * @return
     * @throws ParseException
     */
    public void setAttributes(Object bean, Map<String,String> attribs) throws ParseException;
}

/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: ElementParserFactory.java,v $
 *  Created on Feb 15, 2009
 *******************************************************************************/
package za.co.softco.text.model;

/**
 * Implement this factory interface to return a parser for a specific type of element
 * @author john
 * @model
 */
public interface ElementParserFactory {

    /**
     * Implement this method to return an element parser that will return a specific  
     * @param <T>
     * @param elementName
     * @param expectedType
     * @return
     */
    public <T> ElementParser<T> getParser(String elementName, Class<T> expectedType);
}

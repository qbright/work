/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: ElementParser.java,v $
 *  Created on Feb 15, 2009
 *******************************************************************************/
package za.co.softco.text.model;

import org.w3c.dom.Element;

/**
 * Implement this interface to parse an XML element
 * @author john
 * @model
 */
public interface ElementParser<T> {

    /**
     * Call this method to parse an XML element
     * @param element
     * @return
     */
    public T parseElement(Element element);
}

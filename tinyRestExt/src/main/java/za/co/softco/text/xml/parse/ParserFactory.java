/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 14 Jan 2011
 *******************************************************************************/
package za.co.softco.text.xml.parse;

import org.w3c.dom.Element;


/**
 * @author john
 * Implement a factor for a root object (typically "Order") which
 * @param <R> Root node type 
 */
public interface ParserFactory<B,R extends B> {

    /**
     * Get the root element parser
     * @return
     */
    public Parser<B,B,B,R> getRootParser();
    
    /**
     * Get a parser for a specific node in the XML document
     * @param parent
     * @param element
     * @param <P>
     * @param <C>
     * @return
     */
    public <P extends B,C extends B> Parser<B,R,P,C> getParser(P parent, Element element);

    /**
     * Get a parser for a specific node in the XML document
     * @param parent
     * @param elementName
     * @param <P>
     * @param <C>
     * @return
     */
    public <P extends B,C extends B> Parser<B,R,P,C> getParser(P parent, String elementName);
}

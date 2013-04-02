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

import java.text.ParseException;

import org.w3c.dom.Element;

/**
 * @author john
 * Implement an instance of NodeParser to return an object for an XML element.
 * The parent argument may be null for root nodes.
 * @param <R> - Type of parsed root node 
 * @param <P> - Type of parsed parent node of nodes on which this parser operates
 * @param <T> - Type to return when the parser parses a node 
 */
public interface Parser<B,R extends B,P extends B,T extends B> {

    /**
     * Parse an XML element as an object 
     * @param state
     * @param node
     * @return
     * @throws ParseException
     */
    public T parse(ParserState<B,R,P> state, Element node) throws ParseException;
    
    /**
     * Set text
     * @param parent
     * @param node
     * @param text
     * @throws ParseException
     */
    public void setText(P parent, T node, String text) throws ParseException;
    
    /**
     * This method is called once all child nodes of a node has been parsed
     * @param root
     * @param parent
     * @param node
     * @throws ParseException
     */
    public void complete(R root, P parent, T node) throws ParseException;
}

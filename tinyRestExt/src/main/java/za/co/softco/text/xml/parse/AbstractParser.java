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

import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;
import org.w3c.dom.Element;

import za.co.softco.text.xml.XMLUtils;
import za.co.softco.util.Utils;

/**
 * Default parser - uses bean manager and method names to build objects
 * @author john
 * @param <R> - Type of parsed root node 
 * @param <P> - Type of parsed parent node of nodes on which this parser operates
 * @param <T> - Type to return when the parser parses a node 
 */
public abstract class AbstractParser<B,R extends B,P extends B,T extends B> implements Parser<B,R,P,T> {

    protected final boolean parseChildren;
    
    protected AbstractParser(boolean parseChildren) {
        this.parseChildren = parseChildren;
    }
    
    protected AbstractParser() {
    	this(true);
    }
    
    /*
     * @see za.co.softco.text.xml.parse.Parser#parseChildren(za.co.softco.text.xml.parse.ParserFactory, java.lang.Object, java.lang.Object, org.w3c.dom.Element, za.co.softco.text.xml.parse.AttributeParserFactory)
     */
    @SuppressWarnings("unchecked")
    public void parseChildren(Element node, ParserState<B,R,T> state) throws ParseException {
        if (state != null && parseChildren) {
        	R root = state.getRoot();
        	T parent = state.getParent();
            if (root == null && parent != null) {
                try {
                    root = (R) parent;
                } catch (ClassCastException e) {
                    Logger.getLogger(AbstractParser.class).error("Could not use parent class as root: " + parent.getClass().getName());
                }
            }
            try {
                for (Element childNode : XMLUtils.getChildren(node)) {
                    Parser<B,R,T,?> childParser = state.getChildParser(childNode);
                    if (childParser != null) {
                        childParser.parse(state, childNode);
                    }
                }
            } catch (XPathExpressionException e) {
                throw Utils.cast(e, ParseException.class);
            }
        }
    }    
 
    /*
     * @see za.co.softco.model.parser.Parser#complete(java.lang.Object, java.lang.Object, java.lang.Object)
     */
    @Override
    public void complete(R root, P parent, T node) throws ParseException {
        // This method does nothing by default and must be overwritten to be active 
    }
    
    /*
     * @see za.co.softco.text.xml.parse.Parser#setText(java.lang.Object, java.lang.Object, java.lang.String)
     */
    @Override
    public void setText(P parent, T node, String text) throws ParseException {
        // This method does nothing by default and must be overwritten to be active 
    }
}

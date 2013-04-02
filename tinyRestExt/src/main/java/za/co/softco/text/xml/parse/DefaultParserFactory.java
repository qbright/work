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

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import za.co.softco.text.xml.XMLParseException;
import za.co.softco.util.Utils;


/**
 * @author john
 *
 */
public class DefaultParserFactory<B,R extends B> implements ParserFactory<B,R> {

    private final Map<State,Parser<B,?,?,?>> parsers = new LinkedHashMap<State,Parser<B,?,?,?>>();
    private final String expectedRootElement;
    
    public DefaultParserFactory(Parser<B,?,?,R> rootParser, String expectedRootElement, AttributeParserFactory attribParser) {
        parsers.put(new State(RootParent.class, "*"), rootParser);
        this.expectedRootElement = expectedRootElement;
    }
    
    public DefaultParserFactory(Parser<B,?,?,R> rootParser, String expectedRootElement) throws ParserConfigurationException {
        this(rootParser, expectedRootElement, new DefaultAttributeParserFactory());
    }
    
    public DefaultParserFactory(Class<R> rootClass, String expectedRootElement, AttributeParserFactory attribParser, boolean parseChildren) {
        this(new DefaultParser<B,B,B,R>(rootClass, parseChildren), expectedRootElement, attribParser);
    }

    public DefaultParserFactory(Class<R> rootClass, String expectedRootElement, boolean parseChildren) throws ParserConfigurationException {
        this(rootClass, expectedRootElement, new DefaultAttributeParserFactory(), parseChildren);
    }

    public DefaultParserFactory(Class<R> rootClass, String expectedRootElement) throws ParserConfigurationException {
        this(rootClass, expectedRootElement, true);
    }

    public DefaultParserFactory() throws ParserConfigurationException {
        this.expectedRootElement = null;
    }

    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Parser<B,B,B,R> getRootParser() {
        return (Parser) parsers.get(new State(RootParent.class, "*"));
    }

    public <P extends B,C extends B> void registerParser(Class<? extends P> parentClass, String childElementName, Parser<B,R,P,C> parser) {
        parsers.put(new State(parentClass, childElementName), parser);
    }
    
    /**
     * Create a default parser from the element class
     * @param elementClass
     * @return
     */
    protected <T extends B> Parser<B,R,?,T> createParser(Class<T> elementClass) {
    	return new DefaultParser<B,R,B,T>(elementClass);
    }
    
    /**
     * Parse an XML structure by specifying the root element
     * @param xmlFile
     * @param xmlRootElement
     * @param useAttributeParser
     * @param locale
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws ParseException
     */
    public R parse(File xmlFile, Element xmlRootElement, boolean useAttributeParser, Locale locale) throws SAXException, IOException, ParserConfigurationException, ParseException {
        if (expectedRootElement != null && !xmlRootElement.getNodeName().equalsIgnoreCase(expectedRootElement))
            throw new ParserConfigurationException("Invalid root name (expected <" + expectedRootElement + ">): " + xmlRootElement.getNodeName());
        @SuppressWarnings({ "unchecked", "rawtypes" })
        Parser<B,B,B,R> parser = (Parser) getRootParser();
        if (parser == null) {
        	@SuppressWarnings({ "unchecked", "rawtypes" })
			Parser<B,B,B,R> tmp = (Parser) getParser(null, xmlRootElement);
        	parser = tmp;
        }
        if (parser == null)
        	throw new ParseException("No parser for element " + xmlRootElement.getNodeName(), 0);
        BasicState<B,R,R> state = (useAttributeParser ? new BasicState<B,R,R>(this, xmlFile, null, locale) : new BasicState<B,R,R>(this, xmlFile, null, new DummyAttributeParserFactory(), locale));
        @SuppressWarnings({ "rawtypes", "unchecked" })
		R result = parser.parse((ParserState) state, xmlRootElement);
        if (result != null)
        	parser.complete(null, null, result);
        for (Runnable task : state.getCompletionTasks()) {
            try {
                task.run();
            } catch (RuntimeException e) {
                throw Utils.cast(e, ParseException.class);
            }
        }
        return result;
    }
    
    /**
     * Parse an XML structure by specifying the root element
     * @param xmlFile
     * @param xmlRootElement
     * @param locale
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws ParseException
     */
    public R parse(File xmlFile, Element xmlRootElement, Locale locale) throws SAXException, IOException, ParserConfigurationException, ParseException {
    	return parse(xmlFile, xmlRootElement, true, locale);
    }
    
    /**
     * Parse XML content supplied by an InputStream
     * @param xml
     * @param locale
     * @return
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws ParseException
     * @throws XMLParseException 
     */
    public R parse(File xml, Locale locale) throws IOException, ParserConfigurationException, ParseException, SAXException, XMLParseException {
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xml);
        } catch (SAXParseException e) {
            throw new XMLParseException(xml.getAbsolutePath(), e);
        } catch (SAXException e) {
            throw new XMLParseException(xml.getAbsolutePath(), e);
        } 
        return parse(xml, doc.getDocumentElement(), locale);
    }
    
    @Override
    public <P extends B, C extends B> Parser<B, R, P, C> getParser(P parent, Element element) {
    	return getParser(parent, element.getNodeName());
    }
    
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public <P extends B,C extends B> Parser<B,R,P,C> getParser(P parent, String elementName) {
        if (parent != null && elementName != null) {
            Parser<B,?,?,?> result = parsers.get(new State(parent.getClass(), elementName));
            if (result != null)
                return (Parser) result;
            for (Map.Entry<State,Parser<B,?,?,?>> p : parsers.entrySet()) {
                Class<?> parentClass = p.getKey().getParentClass();
                String childName = p.getKey().getChildElementName();
                if (parentClass == null || childName == null)
                    continue;
                if (parentClass.isAssignableFrom(parent.getClass()) && elementName.equalsIgnoreCase(childName)) {
                    parsers.put(new State(parentClass, elementName), p.getValue());
                    return (Parser) p.getValue();
                }
            }
        } else if (elementName != null) {
        	Parser<B,R,P,C> result = (Parser) parsers.get(new State(null, elementName));
        	if (result != null)
        		return result;
            for (Map.Entry<State,Parser<B,?,?,?>> p : parsers.entrySet()) {
            	String childName = p.getKey().getChildElementName(); 
            	if (childName != null && elementName.equalsIgnoreCase(childName)) {
                    parsers.put(new State(null, elementName), p.getValue());
                    return (Parser) p.getValue();
            	}
            }
        } 
        return (Parser) parsers.get(new State(null, elementName));
    }
    
    /**
     * This class is used to indicate the parser for the root element
     * @author john
     */
    private static class RootParent {
        // Dummy class
    }
    
    /**
     * State for which a parser is defined. A state is typically made up
     * of a parent class (class of node representing parent node)
     * as well as the name of the child element.
     * @author john
     */
    private static class State {
        private final Class<?> parentClass;
        private final String childElementName;
        
        public State(Class<?> parentClass, String childElementName) {
            this.parentClass = parentClass;
            this.childElementName = childElementName.trim().toLowerCase();
        }

        public Class<?> getParentClass() {
            return parentClass;
        }
        
        public String getChildElementName() {
            return childElementName;
        }
        
        @Override
        public int hashCode() {
            return (parentClass != null ? parentClass.hashCode() : 0) + childElementName.hashCode();
        }
        
        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof State))
                return false;
            State ref = (State) obj;
            if (ref.parentClass != parentClass)
                return false;
            return ref.childElementName.equals(childElementName);
        }
        
        @Override
        public String toString() {
            return (parentClass != null ? parentClass.getSimpleName() + " -> " : "") + childElementName;
        }
    }
}

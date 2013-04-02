/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 14 Oct 2012
 *******************************************************************************/
package za.co.softco.text.xml.parse;

import java.text.ParseException;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;

import za.co.softco.text.xml.XMLUtils;
import za.co.softco.util.Utils;

/**
 * @author john
 *
 */
public class ClassNameParser<B,R extends B,P extends B,T extends B> extends DefaultParser<B,R,P,T> {

    /**
     * Constructor
     * @param elementClass
     * @param addToParentMethod
     * @param setParentMethod
     * @param setTextMethod
     * @param parseChildren
     * @throws ParserConfigurationException
     */
    public ClassNameParser(Class<T> elementClass, String addToParentMethod, String setParentMethod, String setTextMethod, boolean parseChildren) throws ParserConfigurationException {
        super(elementClass, addToParentMethod, setParentMethod, setTextMethod, parseChildren);
    }

    /**
     * Constructor
     * @param elementClass
     * @param parseChildren
     * @throws ParserConfigurationException
     */
    public ClassNameParser(Class<T> elementClass, boolean parseChildren) {
        super(elementClass, parseChildren);
    }

    /**
     * Constructor
     * @param elementClass
     * @throws ParserConfigurationException
     */
    public ClassNameParser(Class<T> elementClass) {
        super(elementClass);
    }
	
    /*
     * @see za.co.softco.text.xml.parse.DefaultParser#parse(java.io.File, za.co.softco.text.xml.parse.ParserFactory, java.lang.Object, java.lang.Object, org.w3c.dom.Element, za.co.softco.text.xml.parse.AttributeParserFactory, java.util.List)
     */
	@Override
	public T parse(ParserState<B,R,P> state, Element node) throws ParseException {
		Map<String,String> attribs = XMLUtils.getAttributes(node);
		String className = Utils.normalize(attribs.get("class"));
		if (className == null)
			className = Utils.normalize(attribs.get("classname"));
		if (className == null)
			className = Utils.normalize(attribs.get("name"));
		if (className == null)
			throw new ParseException("Element <" + node.getNodeName() +"> is expected to have \"class\" property", 0);
		
		try {
			@SuppressWarnings("unchecked")
			Class<T> clazz = (Class<T>) Class.forName(className);
			Parser<B,R,P,T> parser = new DefaultParser<B,R,P,T>(clazz, addToParentMethod, setParentMethod, setTextMethod, parseChildren);
			return parser.parse(state, node);
		} catch (ClassNotFoundException e) {
			throw new ParseException("Class not found: " + className, 0);
		}
	}

}

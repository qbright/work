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
 * Default implementation of AttributeParserFactory
 * @author john
 */
public class DummyAttributeParserFactory implements AttributeParserFactory {

	private static final AttributeParser<?> PARSER = new AttributeParser<Object>() {
		@Override
		public Object parse(String value) throws ParseException {
			return null;
		}

		@Override
		public String toString(Object value) {
			return (value != null ? value.toString() : null);
		}
	};
	
    /*
     * @see za.co.softco.model.parser.AttributeParserFactory#getParser(java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> AttributeParser<T> getParser(Class<T> type) throws ParseException {
        return (AttributeParser<T>) PARSER;
    }

    /*
     * 
     * @see za.co.softco.model.parser.AttributeParserFactory#parse(java.lang.String, java.lang.Class)
     */
    @Override
    public <T> T parse(String value, Class<T> type) throws ParseException {
        return null;
    }
    
    /*
     * @see za.co.softco.model.parser.AttributeParserFactory#setAttributes(java.lang.Object, java.util.Map)
     */
    @Override
    public void setAttributes(Object bean, Map<String, String> attribs) {
    	// Do nothing
    }
}

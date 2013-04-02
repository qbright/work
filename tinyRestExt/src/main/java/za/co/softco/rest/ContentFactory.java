/*******************************************************************************
 * Copyright (C) Bester Consulting 2010. All Rights reserved.
 * This file may be distributed under the Softco Share License
 * 
 * @author      John Bester
 * Project:     SoftcoRest
 * Description: HTTP REST Server
 *
 * Changelog  
 *  $Log$
 *  Created on 20 Nov 2009
 *******************************************************************************/
package za.co.softco.rest;

import static za.co.softco.rest.http.HttpConstants.MIME_DEFLATED_XML;
import static za.co.softco.rest.http.HttpConstants.MIME_HTTP_POST;
import static za.co.softco.rest.http.HttpConstants.MIME_PLAIN;
import static za.co.softco.rest.http.HttpConstants.MIME_XML;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import za.co.softco.rest.content.DeflatedXMLContentParser;
import za.co.softco.rest.content.HttpQueryParser;
import za.co.softco.rest.content.PlainTextParser;
import za.co.softco.rest.content.XMLContentParser;
import za.co.softco.rest.model.ContentParser;

/**
 * Register a parser to parse a content object based on the 
 * mime type in the request header
 * @author john
 */
public class ContentFactory {

	private static ContentFactory instance;
	private final Map<String,ContentParser<?>> parsers = new HashMap<String,ContentParser<?>>();
	private final ContentParser<?> PLAIN_TEXT_PARSER = new PlainTextParser();
	public final ContentParser<Element> XML_PARSER = new XMLContentParser();
    public final ContentParser<Element> DEFLATED_XML_PARSER = new DeflatedXMLContentParser();
	//public final ContentParser<Document> HTML_PARSER = new HTMLContentParser();
    public final ContentParser<Map<String,Object>> HTTP_POST_PARSER = new HttpQueryParser();
	
	private ContentFactory() {
		parsers.put(MIME_PLAIN, PLAIN_TEXT_PARSER);
		parsers.put(MIME_XML, XML_PARSER);
        parsers.put(MIME_DEFLATED_XML, DEFLATED_XML_PARSER);
		//parsers.put(MIME_HTML, HTML_PARSER);
        parsers.put(MIME_HTTP_POST, HTTP_POST_PARSER);
	}
	
	public static ContentFactory getInstance() {
		if (instance == null)
			instance = new ContentFactory();
		return instance;
	}

	public ContentParser<?> getContentParser(String contentType) {
		if (contentType == null)
			return PLAIN_TEXT_PARSER;
		
		ContentParser<?> result = parsers.get(contentType.trim().toLowerCase());
		return (result != null ? result : PLAIN_TEXT_PARSER);
	}
}

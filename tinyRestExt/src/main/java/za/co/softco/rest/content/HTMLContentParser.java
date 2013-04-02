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
package za.co.softco.rest.content;

import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import za.co.softco.rest.model.ContentParser;

/**
 * Parser registered by default to parse HTML content as a w3c document.
 * @author john
 * @deprecated - Use HTML content as text in stead
 */
@Deprecated
public class HTMLContentParser implements ContentParser<Document> {

    @Override
    public Document parse(byte[] data, int offset, int length) throws Exception {
        if (data == null)
            throw new ParseException("No data", 0);

        InputStream in = new ByteArrayInputStream(data, offset, length);
        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
        return doc;
    }
    
	@Override
	public Document parse(char[] text, int offset, int length) throws ParseException, SAXException, IOException, ParserConfigurationException {
		if (text == null)
			throw new ParseException("No data", 0);

		InputStream in = new ByteArrayInputStream(new String(text, offset, length).getBytes());
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
        return doc;
	}

	@Override
	public void write(Document doc, OutputStream out) throws Exception {
        // transform the Document into a String
        DOMSource domSource = new DOMSource(doc);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
        StreamResult sr = new StreamResult(out);
        transformer.transform(domSource, sr);
	}
	
	@Override
	public void write(Document doc, BufferedWriter out) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException {
        // transform the Document into a String
        DOMSource domSource = new DOMSource(doc);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
        StreamResult sr = new StreamResult(out);
        transformer.transform(domSource, sr);
	}

}

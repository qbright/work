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
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
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

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import za.co.softco.rest.model.ContentParser;
import za.co.softco.text.xml.XMLUtils;

/**
 * This parser parses bytes as XML and returns a w3c document.
 * @author john
 */
public class XMLContentParser implements ContentParser<Element> {

    private static final boolean DEBUG = true;

    /*
     * @see za.co.softco.rest.model.ContentParser#parse(byte[], int, int)
     */
    @Override
    public Element parse(byte[] data, int offset, int length) throws ParseException, SAXException, IOException, ParserConfigurationException {
        InputStream in = new ByteArrayInputStream(data, offset, length);
        try { 
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            if (DEBUG) {
                try {
                    Logger logger = Logger.getLogger(XMLContentParser.class);
                    if (logger.isDebugEnabled()) 
                        logger.debug("XML received:\r\n" + XMLUtils.toXML(doc));
                } catch (Exception e) {
                    // Ignore exception - content is logged before, just not formatted
                }
            }
            return doc.getDocumentElement();
        } finally {
            in.close();
        }
    }
    
    /*
     * @see za.co.softco.rest.model.ContentParser#parse(char[], int, int)
     */
	@Override
	public Element parse(char[] text, int offset, int length) throws ParseException, SAXException, IOException, ParserConfigurationException {
		if (text == null)
			throw new ParseException("No data", 0);

		String xml = new String(text, offset, length);
		byte[] data = xml.getBytes();
		return parse(data, 0, data.length);
	}

	/*
	 * @see za.co.softco.rest.model.ContentParser#write(java.lang.Object, java.io.OutputStream)
	 */
	@Override
    public void write(Element root, OutputStream out) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, Charset.forName("UTF-8")));
        write(root, writer);
    }

	/*
	 * @see za.co.softco.rest.model.ContentParser#write(java.lang.Object, java.io.BufferedWriter)
	 */
	@Override
	public void write(Element root, BufferedWriter out) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, IOException {
        // transform the Document into a String
        DOMSource domSource = new DOMSource(root.getOwnerDocument());
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING,"UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        StreamResult sr = new StreamResult(out);
        transformer.transform(domSource, sr);
        if (DEBUG) {
            try {
                StringWriter logOut = new StringWriter();
                StreamResult logsr = new StreamResult(logOut);
                transformer.transform(domSource, logsr);
                Logger.getLogger(XMLContentParser.class).debug("Returned XML content:\r\n" + logOut.toString());
            } catch (Exception e) {
                // Ignore exception - content is logged before, just not formatted
            }
        }
	}
}

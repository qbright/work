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
import java.text.ParseException;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

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
 * Parser registered by default to parse a deflated XML request body
 * @author john
 */
public class DeflatedXMLContentParser implements ContentParser<Element> {

    private static final boolean DEBUG = true;
    
    @Override
    public Element parse(byte[] data, int offset, int length) throws Exception {
        if (data == null)
            throw new ParseException("No data", 0);

        InputStream in = new ByteArrayInputStream(data, offset, length);
        in = new InflaterInputStream(in);
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(in);
            if (DEBUG) {
                try {
                    Logger logger = Logger.getLogger(DeflatedXMLContentParser.class);
                    if (logger.isDebugEnabled()) 
                        logger.debug("Compressed XML received:\r\n" + XMLUtils.toXML(doc));
                } catch (Exception e) {
                    // Ignore exception - content is logged before, just not formatted
                }
            }
            return doc.getDocumentElement();
        } finally {
            in.close();
        }
    }
    
	@Override
	public Element parse(char[] text, int offset, int length) throws ParseException, SAXException, IOException, ParserConfigurationException {
	    throw new UnsupportedOperationException("Only parse(byte[], int, int) is supported");
	}

    @Override
    public void write(Element root, OutputStream out) throws ParserConfigurationException, TransformerFactoryConfigurationError, TransformerException, IOException {
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new DeflaterOutputStream(out)));
        write(root, writer);
    }
    
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
                Logger.getLogger(DeflatedXMLContentParser.class).debug("Returned XML content:\r\n" + logOut.toString());
            } catch (Exception e) {
                // Ignore exception - content is logged before, just not formatted
            }
        }
	}
}

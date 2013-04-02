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
 *  Created on 19 Nov 2009
 *******************************************************************************/
package za.co.softco.rest;

import static za.co.softco.rest.http.HttpConstants.DEFAULT_ENCODING;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Element;

import za.co.softco.rest.model.Context;

/**
 * This class eats REST requests and write back a default response. It can
 * be used to test a connection.
 * @author john
 */
public class DummyRestHandler implements RestHandler {

    /*
     * @see za.co.softco.rest.RestHandler#getServiceName()
     */
	@Override
	public String getServiceName() {
		return "Test";
	}

	/*
	 * @see za.co.softco.rest.RestHandler#handle(za.co.softco.rest.RestRequest)
	 */
	@Override
	public void handle(Context request) throws Exception {
		switch (request.getContentType()) {
		case PLAIN:
			request.writeHeader(request.getContentLength(), request
					.getContentType().getMimeType(), DEFAULT_ENCODING, null, null);
			request.writeContent(request.getContentText());
			break;
		case HTML:
			request.writeHeader(request.getContentLength(), request
					.getContentType().getMimeType(), DEFAULT_ENCODING, null, null);
			request.writeContent(request.getContentText());
			break;
		case XML:
			Element root = (Element) request.getContentObject();
			request.writeHeader(request.getContentLength(), request
					.getContentType().getMimeType(), DEFAULT_ENCODING, null, null);
			request.writeXmlReply(root);
			break;
		default:
			request.writeHeader(request.getContentLength(), request
					.getContentType().getMimeType(), DEFAULT_ENCODING, null, null);
			request.writeContent(request
					.getContentText());
			break;
		}
	}

	/*
	 * @see za.co.softco.rest.RestHandler#getServiceVersions()
	 */
    @Override
    public Map<String,String> getServiceVersions() {
        Map<String,String> result = new HashMap<String,String>(5);
        result.put(getServiceName(), "1.0");
        return result;
    }

}

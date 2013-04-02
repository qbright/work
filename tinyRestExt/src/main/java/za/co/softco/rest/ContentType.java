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

import static za.co.softco.rest.http.HttpConstants.*;

/**
 * Known content types (used to register known content parsers)
 * @author john
 */
public enum ContentType {
	PLAIN(MIME_PLAIN),
	HTML(MIME_HTML),
	XML(MIME_XML),
	HTTP_POST(MIME_HTTP_POST),
    DEFLATED_XML(MIME_DEFLATED_XML),
	UNKNOWN("?");
	
	private final String mimeType;
	
	private ContentType(String mimeType) {
		this.mimeType = mimeType.trim().toLowerCase();
	}
	
	public String getMimeType() {
		return mimeType;
	}
	
	public static ContentType get(String mimeType) {
		if (mimeType == null)
			return PLAIN;
		mimeType = mimeType.trim().toLowerCase();
		int split = mimeType.indexOf(';');
		if (split >= 0)
		    mimeType = mimeType.substring(0, split);
		for (ContentType type : values())
			if (type.mimeType.equalsIgnoreCase(mimeType))
				return type;
		return UNKNOWN;
	}
}

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
import java.io.OutputStream;

import za.co.softco.rest.model.ContentParser;

/**
 * This parser simply decodes bytes to a string. Bytes are treated as UTF8
 * encoded text.
 * @author john
 */
public class PlainTextParser implements ContentParser<String> {

    @Override
    public String parse(byte[] data, int offset, int length) throws Exception {
        if (data == null)
            return "";
        return new String(data, offset, length);
    }
    
	@Override
	public String parse(char[] text, int offset, int length) throws Exception {
		if (text == null)
			return "";
		return new String(text, offset, length);
	}

	@Override
	public void write(String text, OutputStream out) throws Exception {
	    if (text != null)
	        out.write(text.getBytes());
	    
	}
	
	@Override
	public void write(String text, BufferedWriter out) throws Exception {
		if (text != null)
			out.write(text);
	}

}

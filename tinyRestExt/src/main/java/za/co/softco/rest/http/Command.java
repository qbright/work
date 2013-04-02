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
package za.co.softco.rest.http;

import static za.co.softco.rest.http.HttpConstants.GET_TOKEN;
import static za.co.softco.rest.http.HttpConstants.HEAD_TOKEN;
import static za.co.softco.rest.http.HttpConstants.POST_TOKEN;
import static za.co.softco.rest.http.HttpConstants.TERMINATE_TOKEN;

/**
 * Enumeration of HTTP commands
 * @author john
 */
public enum Command { 
	GET(GET_TOKEN), 
	POST(POST_TOKEN), 
	HEAD(HEAD_TOKEN), 
	TERMINATE(TERMINATE_TOKEN);
	
	public final String stoken;
	public final byte[] token;
	
	private Command(String token) {
		this.stoken = token;
		this.token = token.getBytes();
	}
	
	public boolean isCommand(byte[] buffer, int offset) {
		if (offset < 0)
			throw new IndexOutOfBoundsException();
		if (offset + token.length >= buffer.length)
			return false;
		for (int i=0; i<token.length; i++)
			if (buffer[offset+i] != token[i])
				return false;
		return true;
	}

	public boolean isCommand(String text) {
		return text.indexOf(stoken) == 0;
	}

	public String getRequest(String line) {
		if (line == null)
			return null;
		String result = line.substring(stoken.length()).trim();
		int pos = result.indexOf(" HTTP");
		if (pos > 0)
			return result.substring(0, pos).trim();
		return result;
	}
	
	public static Command find(byte[] buffer, int offset) {
		for (Command cmd : values())
			if (cmd.isCommand(buffer, offset))
				return cmd;
		return null;
	}

	public static Command find(String line) {
		for (Command cmd : values())
			if (cmd.isCommand(line))
				return cmd;
		return null;
	}
}
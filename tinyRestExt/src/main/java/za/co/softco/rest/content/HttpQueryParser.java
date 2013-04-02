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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import za.co.softco.rest.model.ContentParser;
import za.co.softco.text.DataParser;
import za.co.softco.util.PropertyMap;
import za.co.softco.util.Utils;

/**
 * This parser parses the query part of a HTTP URL and returns the parameters
 * as a case insensitive Map<String,Object> and is used to interpret the
 * HTTP request
 * @author john
 */
public class HttpQueryParser implements ContentParser<Map<String,Object>> {

    private static final String SPLIT = Pattern.quote("&"); 
    private static final String EQUALS = Pattern.quote("=");
    private static final byte[] SPLIT_BYTES = { (byte) '&' };
    private static final byte[] EQUALS_BYTES = { (byte) '=' };
    
    @Override
    public Map<String,Object> parse(byte[] data, int offset, int length) throws Exception {
        if (data == null)
            return parse("");
        return parse(new String(data, offset, length, "UTF-8"));
    }
    
	@Override
	public Map<String,Object> parse(char[] text, int offset, int length) throws Exception {
		if (text == null)
            return parse("");
		return parse(new String(text, offset, length));
	}

    public Map<String,Object> parse(String text) {
        return parseQuery(text);
    }
	
	public static Map<String,Object> parseQuery(String text) {
	    text = Utils.normalize(text);
	    Map<String,Object> result = new PropertyMap<Object>();
	    if (text == null)
	        return result;
	    String[] params = text.split(SPLIT);
	    for (String param : params) {
	        if (param == null)
	            continue;
	        String[] parts = param.split(EQUALS);
	        if (parts == null || parts.length == 0)
	            continue;
	        String name = Utils.normalize(parts[0]);
	        if (name == null)
	            continue;
	        Object value = (parts.length > 1 ? Utils.normalize(parts[1]) : null);
            try {
                name = URLDecoder.decode(name, "UTF-8");
                if (value instanceof String)
                    value = URLDecoder.decode(value.toString(), "UTF-8");
                result.put(name, value);
            } catch (UnsupportedEncodingException e) {
                Logger.getLogger(HttpQueryParser.class).error(e);
            }
	    }
	    return result;
	}
	
	@Override
	public void write(Map<String,Object> data, OutputStream out) throws Exception {
	    if (data == null)
	        return;
	    int i=0;
	    for (Map.Entry<String,Object> param : data.entrySet()) {
	        if (i++ > 0)
	            out.write(SPLIT_BYTES);
	        String name = Utils.normalize(param.getKey());
	        if (name == null)
	            continue;
            String value = Utils.normalize(DataParser.format(param.getValue()));
            if (value == null)
                value = "";
	        out.write(URLEncoder.encode(name, "UTF-8").getBytes("UTF-8"));
            out.write(EQUALS_BYTES);
            out.write(URLEncoder.encode(value, "UTF-8").getBytes("UTF-8"));
	    }
	}
	
	@Override
	public void write(Map<String,Object> data, BufferedWriter out) throws Exception {
        if (data == null)
            return;
        int i=0;
        for (Map.Entry<String,Object> param : data.entrySet()) {
            if (i++ > 0)
                out.write('&');
            String name = Utils.normalize(param.getKey());
            if (name == null)
                continue;
            String value = Utils.normalize(DataParser.format(param.getValue()));
            if (value == null)
                value = "";
            out.write(URLEncoder.encode(name, "UTF-8"));
            out.write('=');
            out.write(URLEncoder.encode(value, "UTF-8"));
        }
	}

}

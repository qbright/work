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
package za.co.softco.rest.model;

import java.io.BufferedWriter;
import java.io.OutputStream;

/**
 * This interface is used to create parsers to parse a POST body as some
 * kind of object that can be handled by the request handler. It also
 * provides a mechanism to stream the response of a request handler. 
 * @author john
 */
public interface ContentParser<T> {

    /**
     * Parse bytes as a specific type of object which can be used
     * by handler.
     * @param data
     * @param offset
     * @param length
     * @return
     * @throws Exception
     */
    public T parse(byte[] data, int offset, int length) throws Exception;

    /**
     * Parse bytes as a specific type of object which can be used
     * by handler.
     * @param text
     * @param offset
     * @param length
     * @return
     * @throws Exception
     * @deprecated - Parsing of byte stream is used in recent versions
     */
    @Deprecated
	public T parse(char[] text, int offset, int length) throws Exception;
	
    /**
     * Streams an object to data stream. This is used to build an HTTP response.
     * @param root
     * @param out
     * @throws Exception
     */
    public void write(T root, OutputStream out) throws Exception;
	
    /**
     * Streams an object to data stream. This is currently used to build an HTTP 
     * response. 
     * @param object
     * @param out
     * @throws Exception
     */
	public void write(T object, BufferedWriter out) throws Exception;
}

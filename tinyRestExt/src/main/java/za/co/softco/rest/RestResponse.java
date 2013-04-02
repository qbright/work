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
 *  Created on 01 Jun 2010
 *******************************************************************************/
package za.co.softco.rest;

import java.io.IOException;

import za.co.softco.io.TextOutputStream;
import za.co.softco.rest.model.Context;

/**
 * A simple REST response object that can be extended to include more
 * functionality specific to a REST service implementation.
 * @author john
 */
public class RestResponse {

    private final Context request;
    private final TextOutputStream out;
    
    /**
     * Constructor
     * @param request
     * @param out
     */
    public RestResponse(Context request, TextOutputStream out) {
        this.request = request;
        this.out = (out != null ? out : new TextOutputStream(request.getOutputStream()));
    }
    
    /**
     * Constructor
     * @param request
     */
    public RestResponse(Context request) {
        this(request, new TextOutputStream(request.getOutputStream()));
    }

    /**
     * Constructor
     * @return
     */
    public Context getRequest() {
        return request; 
    }

    /**
     * Return the response output stream
     * @return
     * @throws IOException
     */
    public TextOutputStream getOutputStream() throws IOException {
        return out;
    }
    
    /**
     * Method must be called when request response was completed to   
     * update stats and other detail (checksums etc) about request
     * @throws IOException
     */
    public void updateStats() throws IOException {
        // Nothing to do - can be overwritten for a specific REST service implementation
    }

    /**
     * Use this method if you calculated your own checksum and content length   
     * @throws IOException
     */
    public void updateStats(String checksum, long length) throws IOException {
        // Nothing to do - can be overwritten for a specific REST service implementation
    }
}

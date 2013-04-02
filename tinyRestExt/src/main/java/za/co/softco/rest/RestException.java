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

import za.co.softco.util.Utils;

/**
 * Exception class used to build an HTTP response with an HTTP status
 * @author john
 */
public class RestException extends Exception {
	private static final long serialVersionUID = 1L;
	private final int httpErrorCode;
	
	/**
	 * Constructor
	 * @param httpErrorCode
	 * @param message
	 */
	public RestException(int httpErrorCode, String message) {
		super(message);
		this.httpErrorCode = httpErrorCode;
	}

	/**
     * Constructor
	 * @param httpErrorCode
	 * @param error
	 */
    public RestException(int httpErrorCode, Throwable error) {
        super(getMessage(error), error);
        setStackTrace(error.getStackTrace());
        this.httpErrorCode = httpErrorCode;
    }

    /**
     * Constructor
     * @param httpErrorCode
     * @param message
     * @param error
     */
    public RestException(int httpErrorCode, String message, Throwable error) {
        super(message, error);
        setStackTrace(error.getStackTrace());
        this.httpErrorCode = httpErrorCode;
    }

    /**
     * Return HTTP status code
     * @return
     */
	public int getHttpErrorCode() {
		return httpErrorCode;
	}
	
	/**
	 * Build an exception message from another exception
	 * @param error
	 * @return
	 */
	private static String getMessage(Throwable error) {
	    if (error == null)
	        return "UNKNOWN ERROR";
	    String msg = Utils.normalize(error.getMessage());
	    return (msg != null ? msg : error.getClass().getName() + " error");
	}
}

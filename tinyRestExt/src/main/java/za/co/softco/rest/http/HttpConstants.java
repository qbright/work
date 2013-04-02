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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Constants which can be used in HTTP protocol
 * @author john
 */
public class HttpConstants {
	public static final DateFormat DATE_FORMAT = new SimpleDateFormat("EEE, d MMM, yyyy HH:mm:ss zzz");
	
	public static final int DEFAULT_PORT = 8080;
    public static final int DEFAULT_SECURE_PORT = 8443;
	public static final int TERMINATE_SIGNAL = 1;
	public static final String TERMINATE_COMMAND = "terminate server";
	public static final int RELEASE_SIGNAL = 2;
	public static final String RELEASE_COMMAND = "worker released";
	public static final String TERMINATE_TOKEN = "$$TERMINATE$$";
	public static final String GET_TOKEN = "GET ";
	public static final String HEAD_TOKEN = "HEAD ";
	public static final String POST_TOKEN = "POST ";
	public static final String LOCALHOST = "localhost";
	public static final String DEFAULT_ENCODING = "UTF-8";

	public static final String MIME_PLAIN = "text/plain";
	public static final String MIME_HTML = "text/html";
    public static final String MIME_CSV = "text/csv";
	public static final String MIME_XML = "text/xml";
    public static final String MIME_PS = "application/postscript";
    public static final String MIME_PDF = "application/pdf";
    public static final String MIME_BINARY = "application/octet-stream";
    public static final String MIME_HTTP_POST = "application/x-www-form-urlencoded";
	public static final String MIME_DEFLATED_XML = "application/deflated-xml";
    public static final String MIME_DEFLATED_CSV = "application/deflated-csv";
    public static final String MIME_DEFLATED_PS = "application/deflated-postscript";
	
    public static final String HEADER_CHECKSUM = "Content-Checksum";
    public static final String HEADER_CHECKSUM_ALGORITHM = "Content-Checksum-Algorithm";
    public static final String HEADER_COMPRESSION = "Content-Compression";
    public static final String HEADER_LAST_GLOBAL_ID = "LastGlobalID";
    public static final String HEADER_LAST_TRANSACTION_ID = "LastTranID";
    
    public static final String REMOTE_HOST = "RemoteHost";
    public static final String REMOTE_PORT = "RemotePort";
    
	public static final String[] INDEX_FILES = { "index.html", "index.htm" };

	public static final String EOL = "\r\n";
	
	private static final Map<Integer,String> messages = new HashMap<Integer,String>();  
	
	/** 2XX: generally "OK" */
	public static final int HTTP_OK = 200;
	public static final int HTTP_CREATED = 201;
	public static final int HTTP_ACCEPTED = 202;
	public static final int HTTP_NOT_AUTHORITATIVE = 203;
	public static final int HTTP_NO_CONTENT = 204;
	public static final int HTTP_RESET = 205;
	public static final int HTTP_PARTIAL = 206;

	/** 3XX: relocation/redirect */
	public static final int HTTP_MULT_CHOICE = 300;
	public static final int HTTP_MOVED_PERM = 301;
	public static final int HTTP_MOVED_TEMP = 302;
	public static final int HTTP_SEE_OTHER = 303;
	public static final int HTTP_NOT_MODIFIED = 304;
	public static final int HTTP_USE_PROXY = 305;

	/** 4XX: client error */
	public static final int HTTP_BAD_REQUEST = 400;
	public static final int HTTP_UNAUTHORIZED = 401;
	public static final int HTTP_PAYMENT_REQUIRED = 402;
	public static final int HTTP_FORBIDDEN = 403;
	public static final int HTTP_NOT_FOUND = 404;
	public static final int HTTP_BAD_METHOD = 405;
	public static final int HTTP_NOT_ACCEPTABLE = 406;
	public static final int HTTP_PROXY_AUTH = 407;
	public static final int HTTP_CLIENT_TIMEOUT = 408;
	public static final int HTTP_CONFLICT = 409;
	public static final int HTTP_GONE = 410;
	public static final int HTTP_LENGTH_REQUIRED = 411;
	public static final int HTTP_PRECON_FAILED = 412;
	public static final int HTTP_ENTITY_TOO_LARGE = 413;
	public static final int HTTP_REQ_TOO_LONG = 414;
	public static final int HTTP_UNSUPPORTED_TYPE = 415;

	/** 5XX: server error */
	public static final int HTTP_SERVER_ERROR = 500;
	public static final int HTTP_INTERNAL_ERROR = 501;
	public static final int HTTP_BAD_GATEWAY = 502;
	public static final int HTTP_UNAVAILABLE = 503;
	public static final int HTTP_GATEWAY_TIMEOUT = 504;
	public static final int HTTP_VERSION = 505;

    public static final int HTTP_CUSTOM_CLIENT_VERSION_NOT_SUPPORTED = 901;
    public static final int HTTP_CUSTOM_CLIENT_TYPE_NOT_SUPPORTED = 902;
	
	static {
		/** 2XX: generally "OK" */
		messages.put(Integer.valueOf(HTTP_OK), "OK");
		messages.put(Integer.valueOf(HTTP_CREATED), "CREATED");
		messages.put(Integer.valueOf(HTTP_ACCEPTED), "Accepted");
		messages.put(Integer.valueOf(HTTP_NOT_AUTHORITATIVE), "Partial Information");
		messages.put(Integer.valueOf(HTTP_NO_CONTENT), "No Response");
		messages.put(Integer.valueOf(HTTP_RESET), "Reset");
		messages.put(Integer.valueOf(HTTP_PARTIAL), "Partial");

		/** XX: relocation/redirect */
		messages.put(Integer.valueOf(HTTP_MULT_CHOICE), "Multiple Choices");
		messages.put(Integer.valueOf(HTTP_MOVED_PERM), "Moved Permanently");
		messages.put(Integer.valueOf(HTTP_MOVED_TEMP), "Found");
		messages.put(Integer.valueOf(HTTP_SEE_OTHER), "See Other");
		messages.put(Integer.valueOf(HTTP_NOT_MODIFIED), "");
		messages.put(Integer.valueOf(HTTP_USE_PROXY), "Use Proxy");

		/** XX: client error */
		messages.put(Integer.valueOf(HTTP_BAD_REQUEST), "Bad request");
		messages.put(Integer.valueOf(HTTP_UNAUTHORIZED), "Unauthorized");
		messages.put(Integer.valueOf(HTTP_PAYMENT_REQUIRED), "PaymentRequired");
		messages.put(Integer.valueOf(HTTP_FORBIDDEN), "Forbidden");
		messages.put(Integer.valueOf(HTTP_NOT_FOUND), "Not found");
		messages.put(Integer.valueOf(HTTP_BAD_METHOD), "Method Not Allowed");
		messages.put(Integer.valueOf(HTTP_NOT_ACCEPTABLE), "Not Acceptable");
		messages.put(Integer.valueOf(HTTP_PROXY_AUTH), "Proxy Authentication Required");
		messages.put(Integer.valueOf(HTTP_CLIENT_TIMEOUT), "Request Timeout");
		messages.put(Integer.valueOf(HTTP_CONFLICT), "Conflict");
		messages.put(Integer.valueOf(HTTP_GONE), "Gone");
		messages.put(Integer.valueOf(HTTP_LENGTH_REQUIRED), "Length Required");
		messages.put(Integer.valueOf(HTTP_PRECON_FAILED), "Precondition Failed");
		messages.put(Integer.valueOf(HTTP_ENTITY_TOO_LARGE), "Request Entity Too Large");
		messages.put(Integer.valueOf(HTTP_REQ_TOO_LONG), "Request-URI Too Long");
		messages.put(Integer.valueOf(HTTP_UNSUPPORTED_TYPE), "Unsupported Media Type");

		/** XX: server error */
		messages.put(Integer.valueOf(HTTP_SERVER_ERROR), "Internal Server Error");
		messages.put(Integer.valueOf(HTTP_INTERNAL_ERROR), "Not implemented");
		messages.put(Integer.valueOf(HTTP_BAD_GATEWAY), "Bad Gateway");
		messages.put(Integer.valueOf(HTTP_UNAVAILABLE), "Service Unavailable");
		messages.put(Integer.valueOf(HTTP_GATEWAY_TIMEOUT), "Gateway Timeout");
		messages.put(Integer.valueOf(HTTP_VERSION), "HTTP Version Not Supported");
	}
	
	public static String getHttpMessage(int code) {
		String result = messages.get(Integer.valueOf(code));
		return result != null ? result : "UNKNOWN CODE " + code;
	}
	
}

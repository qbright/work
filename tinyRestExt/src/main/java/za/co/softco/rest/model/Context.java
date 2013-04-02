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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Map;

import org.w3c.dom.Element;

import za.co.softco.rest.ContentType;
import za.co.softco.rest.PacketStats;
import za.co.softco.rest.content.DataFormat;
import za.co.softco.rest.http.Command;
import za.co.softco.rest.http.Compression;

/**
 * Request context that can be accessed by any request handler
 * @author john
 */
public interface Context {

	/**
	 * Return the request command (GET, POST etc)
	 * @return
	 */
	public abstract Command getCommand();

	/**
	 * Return the request URI
	 * @return
	 */
	public abstract URL getURL();

	/**
	 * Return the report socket address
	 * @return
	 */
	public abstract SocketAddress getRemoteAddress();

	/**
	 * Return the remote host address
	 * @return
	 */
	public abstract InetAddress getRemoteHost();

	/**
	 * Return the remote port
	 * @return
	 */
	public abstract int getRemotePort();

	/**
	 * Return HTTP parameters
	 * @return
	 */
	public abstract Map<String, String> getHttpParameters();

	/**
	 * Return the query parameters
	 * @return
	 */
	public abstract Map<String, Object> getQueryParameters();

	/**
	 * Return the HTTP referencing URL
	 * @return
	 * @throws MalformedURLException
	 */
	public abstract URL getReferringURL() throws MalformedURLException;

	/**
	 * Return the HTTP origin
	 * @return
	 * @throws MalformedURLException
	 */
	public abstract URL getOrigin() throws MalformedURLException;

	/**
	 * Return the host socket object for direct data access
	 * @return
	 */
	public abstract InetSocketAddress getHostSocket();

	/**
	 * Set the item associated with a request. An item is basically a parameter that 
	 * forms part of the URL path and not the URL query. For example, you can include 
	 * a fax number in a URL path as follows:
	 * http://someServer/rest/fax/sendFax/02389292
	 * @param item
	 */
	public abstract void setItem(String item);

	/**
	 * Return the item associated with a request. An item is basically a parameter that 
	 * forms part of the URL path and not the URL query. For example, you can include a 
	 * fax number in a URL path as follows:
	 * http://someServer/rest/fax/sendFax/02389292
	 * @return
	 */
	public abstract String getItem();

	/**
	 * Return the content type as a string
	 * @return
	 */
	public abstract String getMimeType();

	/**
	 * Return the content type as an enumerated value
	 * @return
	 */
	public abstract ContentType getContentType();

	/**
	 * Return the content checksum algorithm
	 * @return
	 */
	public abstract String getResponseChecksumAlgorithm();

	/**
	 * Return the content checksum algorithm
	 * @return
	 */
	public abstract String getContentChecksumAlgorithm();

	/**
	 * Return the content checksum
	 * @return
	 */
	public abstract String getContentChecksum();

	/**
	 * Return the content length
	 * @return
	 */
	public abstract int getContentLength();

	/**
	 * Return the filename of a file upload request
	 * @return
	 */
	public abstract String getFilename();

	/**
	 * Return the raw content of the request
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	public abstract byte[] getRawContent() throws IOException,
			InterruptedException;

	/**
	 * Return the POST content as text 
	 * @return
	 * @throws Exception
	 */
	public abstract String getContentText() throws Exception;

	/**
	 * Return the output stream to which to write the HTTP response
	 * @return
	 */
	public abstract OutputStream getOutputStream();

	/**
	 * Return the POST body parsed as an object based on a predefined parser
	 * which is selected from the mime type dedined in "Content-Type" of the
	 * HTTP header
	 * @return
	 * @throws Exception
	 */
	public abstract Object getContentObject() throws Exception;

	/**
	 * Return the POST body compression algorithm defined in "Compression" in the HTTP request header
	 * @return
	 */
	public abstract Compression getCompression();

	/**
	 * Set the compression algorithm that should be used to uncompress the POST body  
	 * @param compression
	 */
	public abstract void setCompression(Compression compression);

	/**
	 * Return the POST body data format
	 * @return
	 */
	public abstract DataFormat getDataFormat();

	/**
	 * Write content to the response output stream
	 * @param targetOut
	 * @param checksumAlgorithm
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 * @throws NoSuchAlgorithmException
	 */
	public abstract PacketStats saveContent(OutputStream targetOut,
			String checksumAlgorithm) throws IOException, InterruptedException,
			NoSuchAlgorithmException;

	/**
	 * Write a complete HTTP response header
	 * @param contentLength
	 * @param contentType
	 * @param filename
	 * @param lastModified
	 * @throws IOException
	 */
	public abstract void writeHeader(long contentLength, String contentType,
			String filename, Date lastModified) throws IOException;

	/**
	 * Write a complete HTTP response header
	 * @param contentLength
	 * @param contentType
	 * @param encoding
	 * @param filename
	 * @param lastModified
	 * @throws IOException
	 */
	public abstract void writeHeader(long contentLength, String contentType,
			String encoding, String filename, Date lastModified)
			throws IOException;

	/**
	 * Write a complete HTTP response header
	 * @param contentType
	 * @param filename
	 * @param lastModified
	 * @param stats
	 * @throws IOException
	 */
	public abstract void writeHeader(String contentType, String filename,
			Date lastModified, PacketStats stats) throws IOException;

	/**
	 * Write a complete HTTP response header
	 * @param contentLength
	 * @param contentType
	 * @param encoding
	 * @param filename
	 * @param lastModified
	 * @param expiryTime
	 * @param asAttachment
	 * @throws IOException
	 */
	public abstract void writeHeader(long contentLength, String contentType,
			String encoding, String filename, Date lastModified,
			Date expiryTime, boolean asAttachment) throws IOException;

	/**
	 * Write a complete HTTP response header
	 * @param contentLength
	 * @param contentType
	 * @param encoding
	 * @param filename
	 * @param lastModified
	 * @param expiryTime
	 * @param asAttachment
	 * @param stats
	 * @throws IOException
	 */
	public abstract void writeHeader(long contentLength, String contentType,
			String encoding, String filename, Date lastModified,
			Date expiryTime, boolean asAttachment, PacketStats stats)
			throws IOException;

	/**
	 * Write an HTTP reply where the body is XML
	 * @param rootElement
	 * @param properties
	 * @throws Exception
	 */
	public abstract void writeXmlReply(String rootElement,
			Map<String, Object> properties) throws Exception;

	/**
	 * Write an HTTP reply where the body is XML
	 * @param root
	 * @throws Exception
	 */
	public abstract void writeXmlReply(Element root) throws Exception;

	/**
	 * Write an HTML body as a HTTP response
	 * @param text
	 * @throws IOException
	 */
	public abstract void writeHtmlReply(String text) throws IOException;

	/**
	 * Write raw data as HTTP response body
	 * @param filename
	 * @param contentType
	 * @param data
	 * @param offset
	 * @param length
	 * @throws IOException
	 */
	public abstract void write(String filename, String contentType,
			byte[] data, int offset, int length) throws IOException;

	/**
	 * Write raw data as HTTP response body
	 * @param filename
	 * @param contentType
	 * @param data
	 * @throws IOException
	 */
	public abstract void write(String filename, String contentType, byte[] data)
			throws IOException;

	/**
	 * Write plain text as HTTP response body
	 * @param text
	 * @throws IOException
	 */
	public abstract void write(String text) throws IOException;

	/**
	 * Write plain text as HTTP response body
	 * @param text
	 * @throws IOException
	 */
	public abstract void writeContent(String text) throws IOException;

	/**
	 * Use data from an input stream to write the response body
	 * @param data
	 * @throws IOException
	 */
	public abstract void writeContent(InputStream data) throws IOException;

	/**
	 * Write binary data as HTTP response body
	 * @param filename
	 * @param contentType
	 * @param data
	 * @param size
	 * @throws Exception
	 */
	public abstract void writeBinaryReply(String filename, String contentType,
			InputStream data, long size) throws Exception;

	/**
	 * Write binary data as HTTP response body
	 * @param filename
	 * @param contentType
	 * @param data
	 * @throws Exception
	 */
	public abstract void writeBinaryReply(String filename, String contentType,
			InputStream data) throws Exception;

}
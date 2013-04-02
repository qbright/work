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

import static za.co.softco.rest.http.HttpConstants.DATE_FORMAT;
import static za.co.softco.rest.http.HttpConstants.DEFAULT_ENCODING;
import static za.co.softco.rest.http.HttpConstants.EOL;
import static za.co.softco.rest.http.HttpConstants.HEADER_CHECKSUM_ALGORITHM;
import static za.co.softco.rest.http.HttpConstants.HTTP_BAD_REQUEST;
import static za.co.softco.rest.http.HttpConstants.HTTP_OK;
import static za.co.softco.rest.http.HttpConstants.HTTP_UNAUTHORIZED;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.SocketAddress;
import java.net.URL;
import java.security.AccessControlException;
import java.security.DigestOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import za.co.softco.io.CounterOuputStream;
import za.co.softco.io.TextInputStream;
import za.co.softco.rest.content.DataFormat;
import za.co.softco.rest.content.HttpQueryParser;
import za.co.softco.rest.http.Command;
import za.co.softco.rest.http.Compression;
import za.co.softco.rest.model.ContentParser;
import za.co.softco.rest.model.Context;
import za.co.softco.text.DataParser;
import za.co.softco.text.IntegerParser;
import za.co.softco.text.html.HtmlUtils;
import za.co.softco.util.PropertyMap;
import za.co.softco.util.Utils;

/**
 * This class represents a REST request and can be used to create 
 * a reflection handler. Typically a reflection handler will have
 * a method like this:
 * public void sendFax(RestRequest request) throws RestException {}
 * @author john
 */
public class RestRequest implements Context {
    private static final Object NULL = new Object();
    private static final Logger logger = Logger.getLogger(RestRequest.class);
    private static final String XML_ENCODING = "UTF8";
	private static final int BLOCK_SIZE = 10240;
	private static final int CONTENT_READ_TIMEOUT_MS = 10000;
	
	private static final String[] HTTP_IDENTIFYING_ELEMENTS = {
		"html", "body", "table", "br", "a", "p"
	};
	
	static {
		for (int i=0; i<HTTP_IDENTIFYING_ELEMENTS.length; i++)
			HTTP_IDENTIFYING_ELEMENTS[i] = HTTP_IDENTIFYING_ELEMENTS[i].trim().toLowerCase();
		Arrays.sort(HTTP_IDENTIFYING_ELEMENTS);
	}
	
	private static final String TAB = Pattern.quote("\t");
	
	private final Map<String,String> responseHeader = new PropertyMap<String>(new LinkedHashMap<String,String>(20));
	private final Command command;
	private final URL url;
	private final Map<String,String> params;
	private final Map<String,Object> queryParams;
	private final TextInputStream in;
	private final OutputStream out;
    private final String checksum;
    private final String checksumAlgorithm;
	private final String mimeType;
    private final String filename;
	private final ContentType contentType;
    private final SocketAddress remoteAddress;

    private final DataFormat format;
	private final int contentLength;
	//private final long timeoutTime;
	private final boolean ignoreContentLength;
    private Compression compression;
	private String item;
	private byte[] rawContent;
	//private final String contentText = "";
	private String authentication;
	private Object contentObject;
	private boolean headerWritten = false;
	private boolean dataWritten = false;
	
	/**
	 * Constructor
	 * @param command
	 * @param url
	 * @param params
	 * @param in
	 * @param out
	 * @param timeoutTime
	 * @param ignoreContentLength
	 * @param defaultCompression
	 * @param remoteAddress
	 * @throws RestException
	 */
	public RestRequest(Command command, URL url, Map<String,String> params, TextInputStream in, OutputStream out, long timeoutTime, boolean ignoreContentLength, Compression defaultCompression, SocketAddress remoteAddress) throws RestException {
	    this.ignoreContentLength = ignoreContentLength;
		this.command = command;
		this.url = url;
		this.params = (params != null ? params : new PropertyMap<String>());
		this.in = in;
        this.out = out;
		//this.timeoutTime = timeoutTime;
        //this.ch = this.params.get("content-type"); 
        PacketStats check = PacketStats.parseChecksum(this.params.get("content-checksum"), this.params.get("content-checksum-algoritm"));
        this.checksum = (check != null ? check.checksum : null);
        this.checksumAlgorithm = (check != null ? check.algorithm : null);
        this.mimeType = this.params.get("content-type"); 
		this.contentType = ContentType.get(this.mimeType);
		this.remoteAddress = remoteAddress;
		this.filename = extractFilename(this.params.get("content-disposition"));

		String tmp = this.params.get("content-length");
		if (tmp == null) 
			tmp = "0";
		int len = 0;
		try {
			len = Integer.parseInt(tmp);
		} catch (NumberFormatException e) {
			throw new RestException(HTTP_BAD_REQUEST, "Invalid content length");
		}
		this.contentLength = len;
		
		this.queryParams = readQuery(url.getQuery());
        this.format = DataFormat.parse(DataParser.format(queryParams.get("format")), DataFormat.TEXT);
        String compress = Utils.normalize(DataParser.format(queryParams.get("compression")));
        if (compress == null)
            compress = Utils.normalize(DataParser.format(queryParams.get("compress")));
        this.compression = Compression.parse(compress, defaultCompression);
	}

	/**
	 * Extract a filename from a content-disposition header entry
	 * @param contentDisposition
	 * @return
	 */
	private static String extractFilename(String contentDisposition) {
        if (contentDisposition == null)
            return null;
        int pos = contentDisposition.toLowerCase().indexOf("filename");
        String start = contentDisposition.substring(0, pos).trim();
        if (start.length() > 0 && !start.endsWith(";"))
            return null;
        String tmp = contentDisposition.substring(pos + 8).trim();
        if (!tmp.startsWith("="))
            return null;
        tmp = tmp.substring(1).trim();
        pos = tmp.indexOf(';');
        if (pos > 0)
            return tmp.substring(0, pos);
        return tmp;
	}
	
	/**
	 * Compress an output stream
	 * @param out
	 * @return
	 * @throws RestException
	 */
	protected OutputStream compress(OutputStream out) throws RestException {
	    try {
    	    switch (getCompression()) {
            case DEFLATE :
                return new DeflaterOutputStream(out);
    	    case GZIP :
    	        return new GZIPOutputStream(out);
            case ZIP :
                return new ZipOutputStream(out);
            default :
                return out;
    	    }
	    } catch (IOException e) {
	        throw new RestException(HTTP_BAD_REQUEST, e.getMessage());
	    }
	}
	
	/**
	 * Uncompress an input stream
	 * @param in
	 * @return
	 * @throws RestException
	 */
    protected InputStream uncompress(InputStream in) throws RestException {
        try {
            switch (getCompression()) {
            case GZIP :
                return new GZIPInputStream(in);
            case ZIP :
                return new ZipInputStream(in);
            default :
                return in;
            }
        } catch (IOException e) {
            throw new RestException(HTTP_BAD_REQUEST, e.getMessage());
        }
    }

    /**
     * Return a mime type extension for compressed content
     * @return
     * @throws RestException
     */
    protected String getCompressionExt() throws RestException {
        switch (getCompression()) {
        case GZIP :
            return "+gzip";
        case ZIP :
            return "+zip";
        default :
            return "";
        }
    }
    
    /**
     * Parse the query portion of a request as a map
     * @param query
     * @return
     */
	private static Map<String,Object> readQuery(String query) {
	    return HttpQueryParser.parseQuery(query);
	}
	
	/*
	 * @see za.co.softco.rest.Context#getCommand()
	 */
	@Override
	public Command getCommand() {
		return command;
	}
	
	/*
	 * @see za.co.softco.rest.Context#getURL()
	 */
	@Override
	public URL getURL() {
		return url;
	}
	
	/*
	 * @see za.co.softco.rest.Context#getRemoteAddress()
	 */
	@Override
	public SocketAddress getRemoteAddress() {
	    return remoteAddress;
	}
	
	/*
	 * @see za.co.softco.rest.Context#getRemoteHost()
	 */
	@Override
	public InetAddress getRemoteHost() {
	    if (!(remoteAddress instanceof InetSocketAddress))
	        return null;
	    return ((InetSocketAddress) remoteAddress).getAddress();
	}
	
	/*
	 * @see za.co.softco.rest.Context#getRemotePort()
	 */
    @Override
	public int getRemotePort() {
        if (!(remoteAddress instanceof InetSocketAddress))
            return 0;
        return ((InetSocketAddress) remoteAddress).getPort();
    }
    
    /**
     * Create a new URL builder that will build a URL which can
     * be sent back to the client in order to call another method
     * in this REST service.
     * @return
     */
	public URLBuilder getURLBuilder() {
	    return new URLBuilder(url, queryParams);
	}

	/**
     * Create a new URL builder that will build a URL which can
     * be sent back to the client in order to call another method
     * in this REST service.
	 * @param path
	 * @return
	 * @throws MalformedURLException
	 */
    public URLBuilder getURLBuilder(String path) throws MalformedURLException {
        return new URLBuilder(url, path, queryParams);
    }
    
	/*
	 * @see za.co.softco.rest.Context#getHttpParameters()
	 */
	@Override
	public Map<String,String> getHttpParameters() {
		return params;
	}
	
	/*
	 * @see za.co.softco.rest.Context#getQueryParameters()
	 */
	@Override
	public Map<String,Object> getQueryParameters() {
		return queryParams;
	}

	/*
	 * @see za.co.softco.rest.Context#getReferringURL()
	 */
	@Override
	public URL getReferringURL() throws MalformedURLException {
	    String result = Utils.normalize(params.get("Referer"));
	    return (result != null ? new URL(result) : null);
	}
	
	/*
	 * @see za.co.softco.rest.Context#getOrigin()
	 */
    @Override
	public URL getOrigin() throws MalformedURLException {
        String result = Utils.normalize(params.get("Origin"));
        return (result != null ? new URL(result) : null);
    }
    
    /*
	 * @see za.co.softco.rest.Context#getHostSocket()
	 */
    @Override
	public InetSocketAddress getHostSocket() {
        String result = Utils.normalize(params.get("Host"));
        if (result == null)
            return new InetSocketAddress(url.getHost(), url.getPort());
        String[] parts = result.split(":");
        String host = (parts != null && parts.length > 0 ? parts[0] : null);
        String sport = (parts != null && parts.length > 1 ? parts[1] : null);
        int port = IntegerParser.toInt(sport);
        port = (port > 0 ? port : url.getPort());
        host = (host != null ? host : url.getHost());
        return new InetSocketAddress(host, port);
    }
    
    /**
     * Return the authentication string
     * @return
     */
    private String getAuthentication() {
        if (authentication == null) {
            String auth = Utils.normalize(params.get("Authorization"));
            if (auth == null) 
                auth = Utils.normalize(url.getUserInfo());
            if (auth == null)
                throw new AccessControlException("Basic authentication required");
            if (auth.toLowerCase().startsWith("basic "))
                authentication = auth.substring("basic ".length()).trim();
            else
                throw new AccessControlException("Basic authentication required");
        }
        return authentication;
    }
    
    /**
     * Authenticate a client
     * @param listener
     * @throws RestException
     */
    public void authenticate(AuthenticationListener listener) throws RestException {
        if (listener == null)
            throw new IllegalArgumentException("Listener is required");
        try {
            String auth = getAuthentication();
            if (auth == null)
                throw new AccessControlException("Authentication required");
            int pos = auth.indexOf(":");
            String username;
            String password;
            if (pos > 0) {
                username = auth.substring(0, pos);
                password = auth.substring(pos+1);
            } else {
                username = auth;
                password = "";
            }
            listener.authenticate(this, username, password);
        } catch (SecurityException e) {
            throw new RestException(HTTP_UNAUTHORIZED, "Authorization requred");
        }
    }

    /*
	 * @see za.co.softco.rest.Context#setItem(java.lang.String)
	 */
	@Override
	public void setItem(String item) {
	    this.item = item;
	}
	
	/*
	 * @see za.co.softco.rest.Context#getItem()
	 */
    @Override
	public String getItem() {
        return item;
    }
    
    /*
	 * @see za.co.softco.rest.Context#getMimeType()
	 */
	@Override
	public String getMimeType() {
		return mimeType;
	}

	/*
	 * @see za.co.softco.rest.Context#getContentType()
	 */
	@Override
	public ContentType getContentType() {
		return contentType;
	}

    /*
	 * @see za.co.softco.rest.Context#getResponseChecksumAlgorithm()
	 */
    @Override
	public String getResponseChecksumAlgorithm() {
        String result = Utils.normalize(DataParser.format(queryParams.get("checksum-algorithm")));
        if (result != null)
            return result;
        return "MD5";
    }
    
	/*
	 * @see za.co.softco.rest.Context#getContentChecksumAlgorithm()
	 */
	@Override
	public String getContentChecksumAlgorithm() {
	    if (checksumAlgorithm != null)
	        return checksumAlgorithm;
	    String result = Utils.normalize(DataParser.format(queryParams.get("checksum-algorithm")));
	    if (result != null)
	        return result;
	    return "MD5";
	}
	
    /*
	 * @see za.co.softco.rest.Context#getContentChecksum()
	 */
	@Override
	public String getContentChecksum() {
	    if (checksum != null)
	        return checksum;
	    return Utils.normalize(DataParser.format(params.get("checksum")));
	}
	
	/*
	 * @see za.co.softco.rest.Context#getContentLength()
	 */
	@Override
	public int getContentLength() {
		return contentLength;
	}
	
	/*
	 * @see za.co.softco.rest.Context#getFilename()
	 */
	@Override
	public String getFilename() {
	    if (filename != null)
	        return filename;
	    return Utils.normalize(DataParser.format(queryParams.get("filename")));
	}
	
	/**
	 * Read content as a byte buffer
	 * @return
	 * @throws IOException
	 * @throws InterruptedException
	 */
	private byte[] readRawContent() throws IOException, InterruptedException {
        int read;
        
        if (contentLength == 0 || ignoreContentLength) {
            if (command != Command.POST)
                return new byte[0];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] tmp = new byte[10240];
            long timeout = System.currentTimeMillis() + CONTENT_READ_TIMEOUT_MS;
            while (System.currentTimeMillis() <= timeout) {
                if (in.available() == 0) {
                    Thread.sleep(250);
                    if (ignoreContentLength)
                        continue;
                }
                int len = in.read(tmp);
                if (len <= 0)
                    break;
                out.write(tmp, 0, len);
                timeout = System.currentTimeMillis() + CONTENT_READ_TIMEOUT_MS;
            }
            return out.toByteArray();
        } else {
            long timeout = System.currentTimeMillis() + CONTENT_READ_TIMEOUT_MS;
            byte[] buf = new byte[contentLength];
            read = 0;
            while (read < buf.length && System.currentTimeMillis() <= timeout) { 
                int len = in.read(buf, read, buf.length-read);
                read += len;
                if (len > 0)
                    timeout = System.currentTimeMillis() + CONTENT_READ_TIMEOUT_MS;
                if (len < 0)
                    break;
                if (read < buf.length) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new IOException("Interrupted");
                    }
                }
            }
            return buf;
        }
	}

	/*
	 * @see za.co.softco.rest.Context#getRawContent()
	 */
    @Override
	public byte[] getRawContent() throws IOException, InterruptedException {
        if (rawContent == null)
            rawContent = readRawContent();
        return (rawContent != null ? rawContent : new byte[0]);
    }
	
	/**
	 * Read the content into a text buffer if it has not been read before
	 * @throws Exception
	 */
	private Object readContentObject() throws Exception {
        if (rawContent == null && contentObject == null)
            rawContent = readRawContent();
        
		if (rawContent == null) {
		    if (contentObject == null) {
		        logger.debug("<< (NO DATA)");
		        contentObject = NULL;
		    }
		    return null;
		}
		
		ContentParser<?> parser = ContentFactory.getInstance().getContentParser(getContentType().getMimeType());
		if (parser != null) {
		    try {
		        Object result = parser.parse(rawContent, 0, rawContent.length);
		        return result;
		    } catch (Exception e) {
		        if (rawContent.length == 0)
		            logger.error("Failed to parse empty REST body");
		        else
		            logger.debug("Failed to parse REST body:\r\n" + new String(rawContent));
		        throw e;
		    }
		}
		return rawContent;
	}
	
	/*
	 * @see za.co.softco.rest.Context#getContentText()
	 */
	@Override
	public String getContentText() throws Exception {
		if (rawContent == null)
			rawContent = readRawContent();
		return (rawContent != null ? new String(rawContent) : "");
	}
	
	/*
	 * @see za.co.softco.rest.Context#getOutputStream()
	 */
	@Override
	public OutputStream getOutputStream() {
	    return out;
	}
	
	/*
	 * @see za.co.softco.rest.Context#getContentObject()
	 */
	@Override
	public Object getContentObject() throws Exception {
        if (contentObject == NULL)
            return null;
        contentObject = readContentObject();
        if (contentObject == null) {
            contentObject = NULL;
            return null;
	    }
		return contentObject;
	}

	/*
	 * @see za.co.softco.rest.Context#getCompression()
	 */
	@Override
	public Compression getCompression() {
	    return compression;
	}

	/*
	 * @see za.co.softco.rest.Context#setCompression(za.co.softco.rest.http.Compression)
	 */
	@Override
	public void setCompression(Compression compression) {
	    this.compression = (compression != null ? compression : Compression.NONE);
	}
	
	/*
	 * @see za.co.softco.rest.Context#getDataFormat()
	 */
    @Override
	public DataFormat getDataFormat() {
        return format;
    }
    
    /*
	 * @see za.co.softco.rest.Context#saveContent(java.io.OutputStream, java.lang.String)
	 */
	@Override
	@SuppressWarnings("resource")
    public PacketStats saveContent(OutputStream targetOut, String checksumAlgorithm) throws IOException, InterruptedException, NoSuchAlgorithmException {
	    if (Utils.normalize(checksumAlgorithm) == null)
	        checksumAlgorithm = "MD5";
	    
	    MessageDigest checksum = null;
	    DigestOutputStream dout = null;
	    CounterOuputStream cout = null;
	    try {
	        checksum = MessageDigest.getInstance(checksumAlgorithm);
	        checksum.reset();
	        dout = new DigestOutputStream(targetOut, checksum);
            cout = new CounterOuputStream(dout);
	    } catch (NoSuchAlgorithmException e) {
	        cout = new CounterOuputStream(targetOut);
	    }
        if (rawContent != null) {
            cout.write(rawContent);
            //Writer wout = new OutputStreamWriter(cout);
            //wout.write(contentText);
            //wout.flush();
        } else if (contentLength == 0 || ignoreContentLength) {
            if (command != Command.POST)
                return PacketStats.EMPTY_PACKET_STATS;

            byte[] tmp = new byte[10240];
            long timeout = System.currentTimeMillis() + CONTENT_READ_TIMEOUT_MS;
            while (System.currentTimeMillis() <= timeout) {
                if (in.available() == 0) {
                    Thread.sleep(250);
                    if (ignoreContentLength)
                        continue;
                }
                int len = in.read(tmp);
                if (len <= 0)
                    break;
                cout.write(tmp, 0, len);
                timeout = System.currentTimeMillis() + CONTENT_READ_TIMEOUT_MS;
            }
        } else {
            long timeout = System.currentTimeMillis() + CONTENT_READ_TIMEOUT_MS;
            long result = 0;
            byte[] buf = new byte[10240];
            while (result < contentLength && System.currentTimeMillis() <= timeout) { 
                int len = in.read(buf, 0, buf.length);
                result += len;
                if (len > 0) {
                    cout.write(buf, 0, len);
                    timeout = System.currentTimeMillis() + CONTENT_READ_TIMEOUT_MS;
                }
                if (result < contentLength) {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new IOException("Interrupted");
                    }
                }
            }
        }
        cout.flush();
        if (checksum != null) {
            return new PacketStats(cout.length(), checksum);
        }
        return new PacketStats(cout.length());
	}
	
	/**
	 * Add an HTTP response header value
	 * @param name
	 * @param value
	 */
	public void addResponseHeader(String name, String value) {
	    name = Utils.normalize(name);
	    if (name != null && value != null)
	        responseHeader.put(name,value);
	}

	/**
     * Add an HTTP response header value
	 * @param name
	 * @param value
	 */
    public void addResponseHeader(String name, long value) {
        name = Utils.normalize(name);
        if (name != null)
            responseHeader.put(name,Long.toString(value));
    }

    /*
	 * @see za.co.softco.rest.Context#writeHeader(long, java.lang.String, java.lang.String, java.util.Date)
	 */
    @Override
	public void writeHeader(long contentLength, String contentType, String filename, Date lastModified) throws IOException {
        writeHeader(contentLength, contentType, DEFAULT_ENCODING, filename, lastModified);
    }
    
    /*
	 * @see za.co.softco.rest.Context#writeHeader(long, java.lang.String, java.lang.String, java.lang.String, java.util.Date)
	 */
    @Override
	public void writeHeader(long contentLength, String contentType, String encoding, String filename, Date lastModified) throws IOException {
        writeHeader(contentLength, contentType, encoding, filename, lastModified, null, false, null);
    }

    /*
	 * @see za.co.softco.rest.Context#writeHeader(java.lang.String, java.lang.String, java.util.Date, za.co.softco.rest.PacketStats)
	 */
    @Override
	public void writeHeader(String contentType, String filename, Date lastModified, PacketStats stats) throws IOException {
        writeHeader(stats.length, contentType, DEFAULT_ENCODING, filename, lastModified, null, false, stats);
    }

    /*
	 * @see za.co.softco.rest.Context#writeHeader(long, java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.util.Date, boolean)
	 */
    @Override
	public void writeHeader(long contentLength, String contentType, String encoding, String filename, Date lastModified, Date expiryTime, boolean asAttachment) throws IOException {
        writeHeader(contentLength, contentType, encoding, filename, lastModified, expiryTime, asAttachment, null);
    }

    /*
	 * @see za.co.softco.rest.Context#writeHeader(long, java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.util.Date, boolean, za.co.softco.rest.PacketStats)
	 */
	@Override
	public void writeHeader(long contentLength, String contentType, String encoding, String filename, Date lastModified, Date expiryTime, boolean asAttachment, PacketStats stats) throws IOException {
		if (headerWritten)
			throw new IOException("Header may not be written more than once");
		headerWritten = true;
		if (dataWritten)
			throw new IOException("Header may not be written after data has been written");
		out.write(("HTTP/1.1 " + HTTP_OK + " OK").getBytes());
		out.write(EOL.getBytes());
		out.write(("Server: " + url.getPath()).getBytes());
		out.write(EOL.getBytes());
		out.write(("Date: " + DATE_FORMAT.format(new Date())).getBytes());
		out.write(EOL.getBytes());
        out.write("Cache-control: no-Cache".getBytes());
        out.write(EOL.getBytes());
        responseHeader.remove("Server");
        responseHeader.remove("Date");
        responseHeader.remove("Cache-control");
		if (contentLength != 0) {
		    out.write(("Content-Length: " + contentLength).getBytes());
		    out.write(EOL.getBytes());
	        responseHeader.remove("Content-Length");
		}
        if (stats != null) {
            if (stats.algorithm != null) {
                out.write(HEADER_CHECKSUM_ALGORITHM.getBytes());
                out.write("Content-Checksum-Algorithm: ".getBytes());
                out.write(stats.algorithm.getBytes());
                out.write(EOL.getBytes());
                responseHeader.remove("Content-Checksum-Algorithm");
            }
            if (stats.checksum != null) {
                out.write("Content-Checksum: ".getBytes());
                out.write(stats.checksum.getBytes());
                out.write(EOL.getBytes());
                responseHeader.remove("Content-Checksum");
            }
        }
		if (lastModified != null) {
			out.write("Last Modified: ".getBytes());
			out.write(HtmlUtils.formatTimeForHttpHeader(lastModified).getBytes());
			out.write(EOL.getBytes());
            responseHeader.remove("Last Modified");
		}
		if (expiryTime != null) {
		    out.write(("Expires: ").getBytes());
            out.write(HtmlUtils.formatTimeForHttpHeader(expiryTime).getBytes());
            out.write(EOL.getBytes());
            responseHeader.remove("Expires");
		}
		
		if (contentType == null) 
			contentType = "text/html";
		if (contentType.startsWith("text/") && !contentType.toLowerCase().contains("charset"))
		    contentType += "; charset=UTF-8";
		
		out.write("Content-type: ".getBytes());
		if (!contentType.toLowerCase().startsWith("text"))
		    encoding = null;
		else
		    encoding = Utils.normalize(encoding);
		
		if (encoding != null)
		    out.write((contentType + "; charset=" + encoding).getBytes());
		else
            out.write(contentType.getBytes());
        out.write(EOL.getBytes());
        responseHeader.remove("Content-type");
	
		if (Utils.normalize(filename) != null) {
	        if (asAttachment) 
	            out.write(("content-disposition: attachment;filename=" + filename).getBytes());
	        else
                out.write(("content-disposition: filename=" + filename).getBytes());
            out.write(EOL.getBytes());
            responseHeader.remove("content-disposition");
		}
		for (Map.Entry<String,String> h : responseHeader.entrySet()) {
		    out.write(h.getKey().getBytes());
            out.write(": ".getBytes());
            out.write(h.getValue().getBytes());
            out.write(EOL.getBytes());
		}
		
        out.write(EOL.getBytes());
        out.flush();
	}

	/**
	 * Build an XML element that can be used to write the content body of the response 
	 * @param rootElement
	 * @param properties
	 * @return
	 * @throws ParserConfigurationException
	 */
	public Element buildXml(String rootElement, Map<String,Object> properties) throws ParserConfigurationException {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
		Element result = doc.createElement(rootElement);
		doc.appendChild(result);
		if (properties == null)
			return result;
		
		for (Map.Entry<String,Object> prop : properties.entrySet()) {
			String key = prop.getKey();
			Object val = prop.getValue();
			String text = (val != null ? val.toString() : null);
			if (text != null && text.length() == 0)
				text = null;
			if (key == null || key.trim().length() == 0) {
				if (text != null)
					result.appendChild(doc.createTextNode(text));
			} else {
				Element el = doc.createElement(key);
				result.appendChild(el);
				if (text != null)
					el.appendChild(doc.createTextNode(text));
			}
		}
		return result;
	}
	
	/*
	 * @see za.co.softco.rest.Context#writeXmlReply(java.lang.String, java.util.Map)
	 */
	@Override
	public void writeXmlReply(String rootElement, Map<String,Object> properties) throws Exception {
		writeXmlReply(buildXml(rootElement, properties));
	}

	/*
	 * @see za.co.softco.rest.Context#writeXmlReply(org.w3c.dom.Element)
	 */
	@Override
	public void writeXmlReply(Element root) throws Exception {
		ContentParser<Element> parser = ContentFactory.getInstance().XML_PARSER;

		ByteArrayOutputStream bawriter = new ByteArrayOutputStream();
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(compress(bawriter), XML_ENCODING));
		parser.write(root, writer);
		writer.flush();
		byte[] buf = bawriter.toByteArray(); 
		
		writeHeader(buf.length, "text/xml" + getCompressionExt(), DEFAULT_ENCODING, null, null);
		int pos = 0;
		while (pos < buf.length) {
		    int block = BLOCK_SIZE;
		    if (pos + block > buf.length)
		        block = buf.length - pos;
		    if (block > 0) {
		        out.write(buf, pos, block);
		        pos += block;
	            out.flush();
		        Thread.sleep(10);
		    } else {
		        Logger.getLogger(RestRequest.class).warn("Invalid state");
		    }
		}
        out.write(EOL.getBytes());
        out.write(EOL.getBytes());
		out.flush();
		dataWritten = true;
	}

	/**
	 * Build HTML from plain text. A <TABLE> is generated where
	 * each new line produces a <TR> and the values separated by
	 * tabs produces <TD> elements.
	 * @param text
	 * @return
	 * @throws IOException
	 */
	public String buildHtml(String text) throws IOException {
		StringBuilder html = new StringBuilder("<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\r\n");
		html.append("<HTML>\r\n");
        html.append("<HEADER>\r\n");
        html.append("<link rel=\"shortcut icon\" href=\"/favicon.ico\" type=\"image/x-icon\"/>\r\n");
        html.append("<link rel=\"icon\" href=\"/favicon.ico\" type=\"image/x-icon\"/>\r\n");
        html.append("</HEADER>\r\n");
		html.append("<BODY>\r\n");
		String line;
		BufferedReader reader = new BufferedReader(new StringReader(text));
		if (text.indexOf('\t')>= 0) {
			html.append("<TABLE border=\"0\" cellspacing=\"0\">\r\n");
			while ((line = reader.readLine()) != null) {
				html.append("<TR>");
				String[] cells = line.split(TAB);
				for (String cell : cells) {
					html.append("<TD>");
					html.append(cell);
					html.append("&nbsp;</TD>");
				}
				html.append("</TR>\r\n");
			}
		} else {
			while ((line = reader.readLine()) != null) {
				html.append(line);
				html.append("<BR>");
			}
		}
		html.append("</BODY></HTML>\r\n");
		return html.toString();
	}

	/**
	 * Returns true if text seems to be HTML
	 * @param text
	 * @return
	 */
	private boolean isHtml(String text) {
		if (text == null)
			return false;
		int ndx = 0;
		while (ndx < text.length()) {
			ndx = text.indexOf('<', ndx);
			if (ndx < 0)
				return false;
			int close = text.indexOf('>', ndx);
			if (close < 0)
				return false;
			
			String tmp = text.substring(ndx+1, close);
			if (tmp.endsWith("/"))
				tmp = tmp.substring(0, tmp.length()-1);
			tmp = tmp.trim().toLowerCase();
			if (Arrays.binarySearch(HTTP_IDENTIFYING_ELEMENTS, tmp) >= 0)
				return true;
		}
		return false;
	}
	
	/*
	 * @see za.co.softco.rest.Context#writeHtmlReply(java.lang.String)
	 */
	@Override
	public void writeHtmlReply(String text) throws IOException {
		if (text == null)
			text = "";
		
		String html = text;
		if (!isHtml(text))
			html = buildHtml(text);
		
		writeHeader(html.length(), "text/html", DEFAULT_ENCODING, null, null);
		out.write(html.getBytes());
		out.write(EOL.getBytes());
		out.flush();
		dataWritten = true;
	}

	/*
	 * @see za.co.softco.rest.Context#write(java.lang.String, java.lang.String, byte[], int, int)
	 */
	@Override
	public void write(String filename, String contentType, byte[] data, int offset, int length) throws IOException {
		if (data == null)
			return;
		
		out.write(data, offset, length);
		out.write(EOL.getBytes());
		out.flush();
		dataWritten = true;
	}

	/*
	 * @see za.co.softco.rest.Context#write(java.lang.String, java.lang.String, byte[])
	 */
	@Override
	public void write(String filename, String contentType, byte[] data) throws IOException {
		write(filename, contentType, data, 0, (data != null ? data.length : 0));
	}

	/*
	 * @see za.co.softco.rest.Context#write(java.lang.String)
	 */
	@Override
	public void write(String text) throws IOException {
		if (text != null)
			out.write(text.getBytes());
		out.write(EOL.getBytes());
		out.flush();
		dataWritten = true;
	}
	
	/*
	 * @see za.co.softco.rest.Context#writeContent(java.lang.String)
	 */
	@Override
	public void writeContent(String text) throws IOException {
		out.write(EOL.getBytes());
		if (text != null) {
			out.write(text.getBytes());
			out.write(EOL.getBytes());
		}
		out.flush();
		dataWritten = true;
	}

	/*
	 * @see za.co.softco.rest.Context#writeContent(java.io.InputStream)
	 */
    @Override
	public void writeContent(InputStream data) throws IOException {
        if (data != null)
            Utils.copy(data, out);
        out.flush();
        dataWritten = true;
    }

    /*
	 * @see za.co.softco.rest.Context#writeBinaryReply(java.lang.String, java.lang.String, java.io.InputStream, long)
	 */
    @Override
	public void writeBinaryReply(String filename, String contentType, InputStream data, long size) throws Exception {
        writeHeader(size, contentType, null, filename, null);
        long pos = 0;
        byte[] buffer = new byte[10240];
        while (pos < size) {
            int len = data.read(buffer);
            if (len > 0) {
                out.write(buffer, 0, len);
                pos += len;
            }
            if (len < 0)
                break;
        }
        if (pos < size)
            throw new IOException("Unexpected end of data stream: " + size + " bytes expected");
        if (data.available() > 0)
            throw new IOException("End of data stream expected: " + size + " bytes already written");
        out.flush();
        dataWritten = true;
    }

    /*
	 * @see za.co.softco.rest.Context#writeBinaryReply(java.lang.String, java.lang.String, java.io.InputStream)
	 */
    @Override
	public void writeBinaryReply(String filename, String contentType, InputStream data) throws Exception {
        writeHeader(0, contentType, null, filename, null);
        byte[] buffer = new byte[10240];
        while (data.available() > 0) {
            int len = data.read(buffer);
            if (len > 0) {
                out.write(buffer, 0, len);
            }
            if (len < 0)
                break;
        }
        out.flush();
        dataWritten = true;
    }

    /**
     * Builder class used to build a URL that will point to some function in this REST service.
     * @author john
     */
    public class URLBuilder {
        private final URL url;
        private final Map<String,Object> parameters = new PropertyMap<Object>(new LinkedHashMap<String,Object>());
        
        /**
         * Constructor
         * @param url
         * @param parameters
         */
        protected URLBuilder(URL url, Map<String,Object> parameters) {
            this.url = url;
            this.parameters.putAll(parameters);
        }

        /**
         * Constructor
         * @param url
         * @param path
         * @param parameters
         * @throws MalformedURLException
         */
        protected URLBuilder(URL url, String path, Map<String,Object> parameters) throws MalformedURLException {
            this.parameters.putAll(parameters);
            path = Utils.normalize(path);
            if (path == null) {
                path = url.getPath();
            } else if (!path.startsWith("/")) {
                String tmp = url.getPath();
                int pos = tmp.lastIndexOf('/');
                if (pos >= 0)
                    path = tmp.substring(0,pos+1) + path;
                else 
                    path = "/" + path;
            }
            StringBuilder ubuild = new StringBuilder(url.getProtocol());
            ubuild.append("://");
            
            String userInfo = Utils.normalize(url.getUserInfo());
            if (userInfo != null) {
                ubuild.append(userInfo);
                ubuild.append('@');
            }
            
            ubuild.append(url.getHost());
            int port = url.getPort();
            if (port > 0) {
                ubuild.append(":");
                ubuild.append(Integer.toString(port));
            }
            ubuild.append(path);
            this.url = new URL(ubuild.toString());
        }

        /**
         * Remove a parameter
         * @param name
         * @return
         */
        public URLBuilder remove(String name) {
            parameters.remove(name);
            return this;
        }
        
        /**
         * Set a parameter value
         * @param name
         * @param value
         * @return
         */
        public URLBuilder set(String name, Object value) {
            parameters.put(name, value);
            return this;
        }
        
        /**
         * Set multiple parameter values
         * @param parameters
         * @return
         */
        public URLBuilder set(Map<String,Object> parameters) {
            this.parameters.putAll(parameters);
            return this;
        }
        
        /**
         * Get the resulting URL
         * @return
         * @throws MalformedURLException
         */
        public URL get() throws MalformedURLException {
            StringBuilder result = new StringBuilder(url.getProtocol());
            result.append("://");
            
            String userInfo = Utils.normalize(url.getUserInfo());
            if (userInfo != null) {
                result.append(userInfo);
                result.append('@');
            }
            
            result.append(url.getHost());
            int port = url.getPort();
            if (port > 0) {
                result.append(":");
                result.append(Integer.toString(port));
            }
            result.append(url.getPath());
            int i=0;
            for (Map.Entry<String,Object> p : parameters.entrySet()) {
                result.append(i++>0 ? '&' : '?');
                result.append(p.getKey());
                result.append('=');
                String val = DataParser.format(p.getValue());
                if (val != null)
                    result.append(val);
            }
            return new URL(result.toString());
        }
        
        /*
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            try {
                return get().toString();
            } catch (MalformedURLException e) {
                return "Error: " + e.getMessage();
            }
        }
    }
}

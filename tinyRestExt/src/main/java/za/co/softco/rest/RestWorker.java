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
import static za.co.softco.rest.http.HttpConstants.HTTP_BAD_METHOD;
import static za.co.softco.rest.http.HttpConstants.HTTP_BAD_REQUEST;
import static za.co.softco.rest.http.HttpConstants.HTTP_INTERNAL_ERROR;
import static za.co.softco.rest.http.HttpConstants.HTTP_NOT_FOUND;
import static za.co.softco.rest.http.HttpConstants.HTTP_OK;
import static za.co.softco.rest.http.HttpConstants.HTTP_UNAUTHORIZED;
import static za.co.softco.rest.http.HttpConstants.HTTP_UNAVAILABLE;
import static za.co.softco.rest.http.HttpConstants.INDEX_FILES;
import static za.co.softco.rest.http.HttpConstants.RELEASE_COMMAND;
import static za.co.softco.rest.http.HttpConstants.RELEASE_SIGNAL;
import static za.co.softco.rest.http.HttpConstants.TERMINATE_COMMAND;
import static za.co.softco.rest.http.HttpConstants.TERMINATE_SIGNAL;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URL;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import za.co.softco.io.TextInputStream;
import za.co.softco.rest.http.Command;
import za.co.softco.rest.http.Compression;
import za.co.softco.rest.http.HttpConstants;
import za.co.softco.rest.model.Context;
import za.co.softco.text.IntegerParser;
import za.co.softco.text.html.HtmlDocument;
import za.co.softco.text.html.HtmlTable;
import za.co.softco.util.Log;
import za.co.softco.util.PropertyMap;
import za.co.softco.util.Resource;
import za.co.softco.util.Utils;

/**
 * Each instance of this RestWorker class can handle one request at a time.
 * When it is done, the worker becomes available to handle another request.
 * @author john
 */
public class RestWorker implements Runnable {
	final static int BUF_SIZE = 2048;

    private static final Logger log = Logger.getLogger(RestWorker.class);
    private static final long UNAVAILABLE_LOG_INTERVAL_MS = 5*60*1000;
    private long lastUnavailableLogged = 0;
	
	/* mapping of file extensions to content-types */
	private static final Map<String,String> map = new HashMap<String,String>();

	static {
		fillMap();
	}

	/* Handler fatory */
	private final RestFactory factory;
	
	/* Root folder */
	private final File root;
	
	/* Communication protocol (e.g. http or https) */
	private final String protocol;
	
	/* Server socket port */
	private final int serverPort;
	
	/* Timeout in milliseconds */
	private final int timeoutMS;
	
	/* Listener to handle termination events */
	private final ActionListener listener;
	
	/* Should a single connection be used for more than one request? */
	private boolean keepConnection;
	
    /* Should content length be ignored? (Read until end of stream for POST body) */
	private final boolean ignoreContentLength;
	
    /* Default compression for response */
    private Compression defaultCompression;
    
	/* buffer to use for requests */
	private byte[] buf;
	
	/* Socket to client we're handling */
	private Socket s;

	/* Stays false until worker thread is terminated */
	private volatile boolean terminated = false;

	private static volatile byte[] icon;
	
	/**
	 * Constrcutor
	 * @param factory
	 * @param root
	 * @param protocol
	 * @param serverPort
	 * @param timeoutMS
	 * @param keepConnection
	 * @param ignoreContentLength
	 * @param listener
	 * @param defaultCompression
	 */
	public RestWorker(RestFactory factory, File root, String protocol, int serverPort, int timeoutMS, boolean keepConnection, boolean ignoreContentLength, ActionListener listener, Compression defaultCompression) {
		this.factory = factory;
		this.root = root;
		this.protocol = protocol;
		this.serverPort = serverPort;
		this.keepConnection = keepConnection;
		this.ignoreContentLength = ignoreContentLength;
		this.listener = listener;
		this.defaultCompression = defaultCompression;
		this.timeoutMS = Math.max(timeoutMS, 1000);
		buf = new byte[BUF_SIZE];
		s = null;
	}

	/**
	 * Set the socket of the request to be handled
	 * @param s
	 */
	synchronized void setSocket(Socket s) {
		this.s = s;
		notify();
	}

	/**
	 * Terminate the worker
	 */
	public void terminate() {
		this.terminated = true;
	}

    /**
     * Checks whether a socket address is a local address
     * @param address
     * @return
     */
    public static boolean isLocalInetAddress(SocketAddress address) {
        if (address instanceof InetSocketAddress) 
            return isLocalInetAddress(((InetSocketAddress) address).getAddress());
        return false;
    }
    
	/**
	 * Checks whether an internet address is a local address
	 * @param address
	 * @return
	 */
    public static boolean isLocalInetAddress(InetAddress address) {
        Enumeration<NetworkInterface> nw;
        try {
            nw = NetworkInterface.getNetworkInterfaces();
            while (nw.hasMoreElements()) {
                NetworkInterface ni = nw.nextElement();
                Enumeration<InetAddress> addresses = ni.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress locadr = addresses.nextElement();
                    if (locadr == null)
                        continue;
                    if (locadr.equals(address))
                        return true;
                }
            }
            return false;
        } catch (SocketException e) {
            log.warn("Could not enumerate network interfaces: " + e.getMessage(), e);
            return false;
        }
    }
    
	/*
	 * @see java.lang.Runnable#run()
	 */
    @Override
	public synchronized void run() {
		while (!terminated) {
			if (s == null) {
				/* nothing to do */
				try {
					wait();
					continue;
				} catch (InterruptedException e) {
				    terminated = true;
				    return;
				}
			}
			try {
			    SocketAddress remote = s.getRemoteSocketAddress();
		        handleClient(remote, new TextInputStream(s.getInputStream()));
			} catch (TerminateException e) {
				if (listener != null)
					listener.actionPerformed(new ActionEvent(this, TERMINATE_SIGNAL, TERMINATE_COMMAND));
			} catch (Exception e) {
			    Throwable err = Log.unwrap(e);
			    String msg = Utils.normalize(err.getMessage());
			    if (msg == null)
			        msg = "ERROR: " + err.getClass().getName();
			    System.err.println(msg);
				e.printStackTrace();
			} finally {
		        if (listener != null)
		            listener.actionPerformed(new ActionEvent(this, RELEASE_SIGNAL, RELEASE_COMMAND));
			}
		}
        if (listener != null)
            listener.actionPerformed(new ActionEvent(this, RELEASE_SIGNAL, RELEASE_COMMAND));
	}

    /**
     * Handle a client request
     * @param remote
     * @param in
     * @throws Exception
     */
	private void handleClient(SocketAddress remote, TextInputStream in) throws Exception {
        OutputStream out = s.getOutputStream();
		try {
		    while (!s.isClosed()) {
    			Command command = null;
    			String line;
    			while ((line = in.readLine()) != null) {
    				line = line.trim();
    				log.debug("<< " + line);
    				command = Command.find(line);
    				if (command != null)
    					break;
    			}
    			if (line == null) {
    				writeErrorResponse(HTTP_BAD_REQUEST, "bad request", out);
    				return;
    			}
    
    			final long timeoutTime = System.currentTimeMillis() + timeoutMS;
    			
    			if (command == null) {
    				/* we don't support this method */
    				String message = "method not allowed: ";
    				int pos = line.indexOf(' ');
    				if (pos > 0)
    					message += line.substring(0, pos);
    				else
    					message += "null";
    				writeErrorResponse(HTTP_BAD_METHOD, message, out);
    				return;
    			}
    
    			String request = command.getRequest(line);
    			Map<String,String> params = new PropertyMap<String>(20);
    			paramloop:
    			while ((line = in.readLine()) != null && line.length() > 0) {
    				String[] parts = line.split(":");
    				if (parts.length == 0) 
    					break paramloop;
    
    				String key = parts[0].trim();
    				switch (parts.length) {
    				case 0:
    				case 1:
    					params.put(key, "");
    					params.put(key.toLowerCase(), "");
    					break;
    				case 2:
    					params.put(key, parts[1]);
    					params.put(key.toLowerCase(), parts[1].trim());
    					break;
    				default:
    					String val = line.substring(parts[0].length()+1).trim();
    					params.put(key, val);
    					params.put(key.toLowerCase(), val);
    					break;
    				}
    			}
    
    			File target = null;
    			if (request != null) {
    				if (request.indexOf('/') == 0) {
    					if (request.length() == 1 || request.charAt(1) != '/')
    						request = request.substring(1);
    				}
    				target = new File(root, request);
    				if (target.exists()) {
    					if (target.isDirectory()) {
    						for (String indexFile : INDEX_FILES) {
    							File ind = new File(target, indexFile);
    							if (ind.exists())
    								target = ind;
    						}
    					}
    				}
    			}
    				
    			try {
        			switch (command) {
        			case GET:
        				if (target != null && target.isFile()) {
        					printHeaders(target, out);
        					sendFile(target, out);
        					break;
        				}
        				try {
        					handleGet(parse(command, request, params), params, out, timeoutTime, remote);
                        } catch (SecurityException e) {
                            log.error(e);
                            writeErrorResponse(HTTP_UNAUTHORIZED, e.getMessage(), out);
                        } catch (RestException e) {
                            switch (e.getHttpErrorCode()) {
                            case HTTP_INTERNAL_ERROR :
                                String stackTrace = Log.stackTraceToText(Log.getOriginalException(e.getCause()));
                                log.error("Internal error: " + e.getMessage() + "\r\n" + stackTrace, e);
                                writeErrorResponse(e.getHttpErrorCode(), e.getMessage(), out);
                                break;
                            case HTTP_UNAVAILABLE :
                                if (lastUnavailableLogged + UNAVAILABLE_LOG_INTERVAL_MS < System.currentTimeMillis()) {
                                    log.error(e);
                                    writeErrorResponse(e.getHttpErrorCode(), e.getMessage(), out);
                                    lastUnavailableLogged = System.currentTimeMillis();
                                }
                                break;
                            default :
                                log.error(e);
                                writeErrorResponse(e.getHttpErrorCode(), e.getMessage(), out);
                            }
        				}
        				break;
        			case HEAD:
        				printHeaders(target, out);
        				out.write(EOL.getBytes());
        				out.write(EOL.getBytes());
        				out.write("Not Found".getBytes());
        				out.write(EOL.getBytes());
                        out.write(EOL.getBytes());
        				out.write("The requested resource was not found.".getBytes());
                        out.write(EOL.getBytes());
        				break;
        			case POST:
        				try {
        					handlePost(parse(command, request, params), params, in, out, timeoutTime, remote);
                        } catch (SecurityException e) {
                            log.error(e);
                            writeErrorResponse(HTTP_UNAUTHORIZED, e.getMessage(), out);
        				} catch (RestException e) {
        				    Throwable cause = e.getCause();
        				    if (cause == null)
        				        cause = e;
        				    cause = Log.unwrap(cause);
        				    String msg = Utils.normalize(cause.getMessage());
        				    if (msg == null)
        				        msg = cause.getClass().getName() + " error";
        				    
                            log.error(msg, e);
        					writeErrorResponse(e.getHttpErrorCode(), e.getMessage(), out);
        				}
        				break;
        			case TERMINATE:
                        if (isLocalInetAddress(remote))
                            throw new TerminateException();
                        log.warn("Ignored terminate command received from unknown address");
        			}
                } catch (RuntimeException e) {
                    String msg = Utils.normalize(e.getMessage());
                    if (msg == null)
                        msg = e.getClass().getName();
                    log.error(msg, e);
                    throw e;
    			} catch (Exception e) {
    			    Throwable err = Log.unwrap(e);
                    String msg = Utils.normalize(err.getMessage());
                    if (msg == null)
                        msg = e.getClass().getName();
    			    log.error(msg, err);
    			    throw e;
    			} finally {
    			    if (!keepConnection) {
        			    try {
        			        s.close();
        			    } catch (IOException e) {
        		            log.warn("Could not close socket: " + e.getMessage(), e);
        			    }
    			    }
    			}
		    }
		} catch (SocketException e) {
		    log.warn(e.getMessage(), e);
		} finally {
			s.close();
			s = null;
		}
	}

	/**
	 * Write an error response to the client
	 * @param code
	 * @param message
	 * @param out
	 * @throws IOException
	 */
	public static void writeErrorResponse(int code, String message, OutputStream out) throws IOException {
        writeErrorResponse(code, message, new OutputStreamWriter(out));
    }

	/**
     * Write an error response to the client
	 * @param code
	 * @param message
	 * @param out
	 * @throws IOException
	 */
	public static void writeErrorResponse(int code, String message, Writer out) throws IOException {
		String html = "<!DOCTYPE HTML PUBLIC \"-//IETF//DTD HTML 2.0//EN\">\r\n"
					+ "<html>\r\n"
					+ "<head><title>" + code + " " + message + "</title></head>\r\n"
					+ "<body>\r\n"
					+ "<H3>HTTP/1.0 " + code + " " + message + "</H3>\r\n" 
					+ "</body>\r\n</html>";
		
		out.write("HTTP/1.1 " + code + " " + HttpConstants.getHttpMessage(code));
		out.write(EOL);
		out.write("Date: " + DATE_FORMAT.format(new Date()));
		out.write(EOL);
		out.write("Server: SoftCo Rest Server");
		out.write(EOL);
		out.write("Content-Type: text/html; charset=" + DEFAULT_ENCODING);
		out.write(EOL);
		out.write("Content-Length: " + html.length());
		out.write(EOL);
        if (code == HTTP_UNAUTHORIZED) {
            out.write("WWW-Authenticate: Basic realm=\"Assembla Trac Restricted Area\"");
            out.write(EOL);
            out.write("WWW-Authenticate: Basic realm=\"Assembla Restricted Area\"");
            out.write(EOL);
        }
		out.write("Connection: close");
		out.write(EOL);
		out.write(EOL);
		out.write(html);
		out.write(EOL);
		out.flush();
	}

	/**
	 * Build HTML to display versions of the different components in the REST service
	 * @return
	 */
	private String getVersionHtml() {
	    HtmlDocument html = new HtmlDocument("REST Server Version");
	    html.addStyleSheet()
	        .addStyle("TH")
            .addProperty("PADDING-LEFT", "4px")
            .addProperty("PADDING-RIGHT", "4px")
            .addProperty("PADDING-BOTTOM", "0")
            .addProperty("PADDING-TOP", "0")
            .addProperty("VERTICAL-ALIGN", "middle")
            .addProperty("FONT-FAMILY", "Franklin Gothic Medium, Verdana, Arial, Helvetica")
            .addProperty("BACKGROUND-COLOR", "#E0E0E0")
            .addProperty("FONT-WEIGHT", "bold")
            .addProperty("TEXT-ALIGN", "left")
            .close()
            .addStyle("TD")
            .addProperty("PADDING-LEFT", "4px")
            .addProperty("PADDING-RIGHT", "4px")
            .addProperty("PADDING-BOTTOM", "0")
            .addProperty("PADDING-TOP", "0")
            .addProperty("VERTICAL-ALIGN", "middle")
            .addProperty("FONT-FAMILY", "Franklin Gothic Medium, Verdana, Arial, Helvetica")
            .addProperty("TEXT-ALIGN", "left")
            .close()
            .close();
	    
	    String desc = Utils.getApplicationDescription(null);
	    HtmlTable table = html.addTable("100%", 0, 0);
	    table.addRow().addCell("<B>Java VM:</B>").close().addCell("<B>" + Utils.getJavaVmDescription() + "</B>");
        if (desc != null) {
            table.addRow().addCell("<B>Service:</B>").close().addCell("<B>" + desc + "</B>");
        }
        html.addLineBreak().addLineBreak();
        
        table = html.addTable("100%", 0, 1, "20%", "80%");
        table.addRow().addHeaderCell("REST Handlers").colSpan(2);
        table.addRow().addHeaderCell("Version").close().addHeaderCell("Handler");
        
        for (Map.Entry<String,String> handlerVersion : factory.getHandlerVersions().entrySet()) {
            table.addRow().addCell(handlerVersion.getValue()).close().addCell(handlerVersion.getKey());
        }
        html.addLineBreak().addLineBreak();
        
        table = html.addTable("100%", 0, 1, "20%", "80%");
        table.addRow().addHeaderCell("REST Services").colSpan(2);
        table.addRow().addHeaderCell("Version").close().addHeaderCell("Service");
        
        for (RestHandler handler : factory.getHandlers()) {
            StringBuilder service = new StringBuilder();
            service.append("/");
            service.append(handler.getServiceName());
            service.append("/");
            for (Map.Entry<String,String> version : handler.getServiceVersions().entrySet()) {
                table.addRow().addCell(version.getValue()).close().addCell(service + version.getKey());
            }
        }
        html.addLineBreak().addLineBreak();
        
        table = html.addTable("100%", 0, 1, "20%", "80%");
        table.addRow().addHeaderCell("Database drivers").colSpan(2).close();
        table.addRow().addHeaderCell("Version").close().addHeaderCell("Driver");
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        if (drivers != null) {
            while (drivers.hasMoreElements()) {
                Driver driver = drivers.nextElement();
                table.addRow().addCell(driver.getMajorVersion() + "." + driver.getMinorVersion()).close().addCell(driver.getClass().getName());
            }
        }
        html.addLineBreak().addLineBreak();

        table = html.addTable("100%", 0, 1, "20%", "80%");
        table.addRow().addHeaderCell("Java libraries").colSpan(2);
        table.addRow().addHeaderCell("Version").close().addHeaderCell("Package");
        
        for (Map.Entry<String,String> jar : Utils.getAllJarVersions().entrySet()) {
            table.addRow().addCell(jar.getValue()).close().addCell(jar.getKey());
        }
        
        return html.toString();
	}
	
	/**
	 * Return the favourite icon
	 * @param request
	 * @throws Exception
	 */
	private void writeDefaultIcon(Context request) throws Exception {
        byte[] tmp = icon;
        if (icon == null) {
            tmp = Resource.loadBinaryResource("images/favicon.ico");
            icon = tmp; 
        }
        if (tmp == null) 
            throw new RestException(HTTP_NOT_FOUND, "Resource not found");

        InputStream in = new ByteArrayInputStream(tmp);
        try {
            request.writeBinaryReply("favicon.ico", "image/x-icon", in, tmp.length);
        } finally {
            in.close();
        }
	}
	
	/**
	 * Handle a HTTP GET
	 * @param request
	 * @param params
	 * @param out
	 * @param timeoutTime
	 * @param remoteAddress
	 * @throws Exception
	 */
	private void handleGet(URL request, Map<String,String> params, OutputStream out, long timeoutTime, SocketAddress remoteAddress) throws Exception {
	    String path = request.getPath();
	    try {
		    if (path != null) {
		        if (path.equalsIgnoreCase("version") || path.equalsIgnoreCase("/version")) {
		            RestRequest req = new RestRequest(Command.GET, request, params, null, out, timeoutTime, ignoreContentLength, defaultCompression, remoteAddress);
		            RestServer.setContext(req);
		            req.writeHtmlReply(getVersionHtml());
		            return;
		        }
	            if (path.equalsIgnoreCase("favicon.ico") || path.equalsIgnoreCase("/favicon.ico")) {
	                RestRequest req = new RestRequest(Command.GET, request, params, null, out, timeoutTime, ignoreContentLength, defaultCompression, remoteAddress);
		            RestServer.setContext(req);
	                writeDefaultIcon(req);
	                return;
	            }
		    }
			RestHandler handler = factory.getHandler(request.getPath());
			if (handler != null) {
				RestRequest req = new RestRequest(Command.GET, request, params, null, out, timeoutTime, ignoreContentLength, defaultCompression, remoteAddress);
	            RestServer.setContext(req);
				handler.handle(req);
			}
	    } finally {
            RestServer.resetContext();
	    }
	}
	
	/**
     * Handle a HTTP POST
	 * @param request
	 * @param params
	 * @param in
	 * @param out
	 * @param timeoutTime
	 * @param remoteAddress
	 * @throws Exception
	 */
	private void handlePost(URL request, Map<String,String> params, TextInputStream in, OutputStream out, long timeoutTime, SocketAddress remoteAddress) throws Exception {
		RestHandler handler = factory.getHandler(request.getPath());
		if (handler != null)
			handler.handle(new RestRequest(Command.POST, request, params, in, out, timeoutTime, ignoreContentLength, defaultCompression, remoteAddress));
	}
	
	/**
	 * Print headers to an output stream
	 * @param targ
	 * @param out
	 * @return
	 * @throws IOException
	 */
    private boolean printHeaders(File targ, OutputStream out) throws IOException {
        return printHeaders(targ, new OutputStreamWriter(out));
    }

    /**
     * Print headers to an output stream
     * @param targ
     * @param out
     * @return
     * @throws IOException
     */
	private boolean printHeaders(File targ, Writer out) throws IOException {
		if (!targ.exists()) {
			writeErrorResponse(HTTP_NOT_FOUND, null, out);
			log.warn("From " + s.getInetAddress().getHostAddress() + ": GET " + targ.getAbsolutePath() + "-->" + HTTP_NOT_FOUND);
			return false;
		} 

		out.write("HTTP/1.0 " + HTTP_OK + " OK");
		out.write(EOL);

		log.info("From " + s.getInetAddress().getHostAddress() + ": GET " + targ.getAbsolutePath() + "-->" + HTTP_OK);
		out.write("Server: Softco Rest Server");
		out.write(EOL);
		out.write("Date: " + DATE_FORMAT.format(new Date()));
		out.write(EOL);
		if (!targ.isDirectory()) {
			out.write("Content-length: " + targ.length());
			out.write(EOL);
			out.write("Last Modified: " + (new Date(targ.lastModified())));
			out.write(EOL);
			String name = targ.getName();
			int ind = name.lastIndexOf('.');
			String ct = null;
			if (ind > 0) 
				ct = map.get(name.substring(ind));

			if (ct == null)
				ct = "unknown/unknown";

			out.write("Content-type: " + ct);
			out.write(EOL);
		} else {
			out.write("Content-type: text/html");
			out.write(EOL);
		}
		return true;
	}

	/**
	 * Respond by sending a file
	 * @param targ
	 * @param out
	 * @throws IOException
	 */
	private void sendFile(File targ, OutputStream out) throws IOException {
		InputStream is = null;
		out.write(EOL.getBytes());
		if (targ.isDirectory()) {
			listDirectory(targ, out);
			return;
		} 
		is = new FileInputStream(targ.getAbsolutePath());

		try {
			int n;
			while ((n = is.read(buf)) > 0)
				out.write(buf, 0, n);
		} finally {
			is.close();
		}
	}

	/**
	 * Set a filename suffix for a mime type
	 * @param k
	 * @param v
	 */
	public static void setSuffix(String k, String v) {
		map.put(k, v);
	}

	/**
	 * Set all filename suffixes for known mime types
	 */
	private static void fillMap() {
		setSuffix("", "content/unknown");
		setSuffix(".uu", "application/octet-stream");
		setSuffix(".exe", "application/octet-stream");
		setSuffix(".ps", "application/postscript");
		setSuffix(".zip", "application/zip");
		setSuffix(".sh", "application/x-shar");
		setSuffix(".tar", "application/x-tar");
		setSuffix(".snd", "audio/basic");
		setSuffix(".au", "audio/basic");
		setSuffix(".wav", "audio/x-wav");
		setSuffix(".gif", "image/gif");
		setSuffix(".jpg", "image/jpeg");
		setSuffix(".jpeg", "image/jpeg");
		setSuffix(".htm", "text/html");
		setSuffix(".html", "text/html");
		setSuffix(".text", "text/plain");
		setSuffix(".c", "text/plain");
		setSuffix(".cc", "text/plain");
		setSuffix(".c++", "text/plain");
		setSuffix(".h", "text/plain");
		setSuffix(".pl", "text/plain");
		setSuffix(".txt", "text/plain");
		setSuffix(".java", "text/plain");
	}

	/**
	 * List files in a directory and print results to an output stream
	 * @param dir
	 * @param out
	 * @throws IOException
	 */
    private void listDirectory(File dir, OutputStream out) throws IOException {
        listDirectory(dir, new OutputStreamWriter(out));
    }

    /**
     * List files in a directory and print results to a writer
     * @param dir
     * @param out
     * @throws IOException
     */
	private void listDirectory(File dir, Writer out) throws IOException {
		out.write("<TITLE>Directory listing</TITLE><P>");
		out.write(EOL);
		out.write("<A HREF=\"..\">Parent Directory</A><BR>");
		out.write(EOL);
		String[] list = dir.list();
		for (int i = 0; list != null && i < list.length; i++) {
			File f = new File(dir, list[i]);
			if (f.isDirectory()) {
				out.write("<A HREF=\"" + list[i] + "/\">" + list[i] + "/</A><BR>");
			} else {
				out.write("<A HREF=\"" + list[i] + "\">" + list[i] + "</A><BR");
			}
			out.write(EOL);
		}
		out.write("<P><HR><BR><I>" + (new Date()) + "</I>");
		out.write(EOL);
		out.flush();
	}
	
	/**
	 * Parse an HTTP request
	 * @param command
	 * @param request
	 * @param params
	 * @return
	 * @throws MalformedURLException
	 */
	private URL parse(Command command, String request, Map<String,String> params) throws MalformedURLException {
		if (request == null || request.trim().length() == 0)
			return new URL(command.toString(), null, null);
		int versionPos = request.indexOf(" HTTP-");
		if (versionPos >= 0)
			request = request.substring(0, versionPos);
		if (request.indexOf("://") >= 0)
			return new URL(request);

		String host = params.get("host");
		if (host == null || host.equals(".") || host.startsWith("127.0.0") || host.equalsIgnoreCase("localhost")) {
            int port = serverPort;
            if (host != null) {
                int pos = host.indexOf(':');
                if (pos >= 0) {
                    int tmp = IntegerParser.toInt(host.substring(pos+1));
                    if (tmp > 0)
                        port = tmp;
                }
            }
			host = "localhost:" + port;
		}

		if (request.startsWith("/"))
			request = request.substring(1);
		
		return new URL(protocol + "://" + host.trim() + "/" + request);
	}
}

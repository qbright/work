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

import static za.co.softco.rest.http.HttpConstants.DEFAULT_SECURE_PORT;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HandshakeCompletedEvent;
import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import za.co.softco.rest.http.Compression;

/**
 * REST server that accepts HTTPS requests
 * @author john
 *
 */
public class SecureRestServer extends RestServer {

    private final SSLContext sslContext;
    
    /**
     * Constructor
     * @param factory
     * @param serverPort
     * @param minWorkerThreads
     * @param maxWorkerThreads
     * @param keepConnection
     * @param ignoreContentLength
     * @param defaultCompression
     * @param sslContext
     * @throws Exception
     */
    public SecureRestServer(RestFactory factory, int serverPort, int minWorkerThreads, int maxWorkerThreads, boolean keepConnection, boolean ignoreContentLength, Compression defaultCompression, SSLContext sslContext) throws Exception {
        super(factory, serverPort, minWorkerThreads, maxWorkerThreads, keepConnection, ignoreContentLength, defaultCompression);
        this.sslContext = sslContext;
    }

    /**
     * Constructor
     * @param serverPort
     * @param minWorkerThreads
     * @param maxWorkerThreads
     * @param keepConnection
     * @param ignoreContentLength
     * @param defaultCompression
     * @param sslContext
     * @param handlers
     * @throws Exception
     */
    public SecureRestServer(int serverPort, int minWorkerThreads, int maxWorkerThreads, boolean keepConnection, boolean ignoreContentLength, Compression defaultCompression, SSLContext sslContext, RestHandler... handlers) throws Exception {
        this(createFactory(handlers), serverPort, minWorkerThreads, maxWorkerThreads, keepConnection, ignoreContentLength, defaultCompression, sslContext);
    }

    /*
     * @see za.co.softco.rest.RestServer#getProtocol()
     */
    @Override
    protected String getProtocol() {
        return "https";
    }
    
    /*
     * @see za.co.softco.rest.RestServer#createServerSocket(int)
     */
    @Override
    protected ServerSocket createServerSocket(int port) throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException, UnrecoverableKeyException, KeyManagementException {
        SSLServerSocketFactory factory;
        if (sslContext != null)
            factory = (SSLServerSocketFactory) sslContext.getServerSocketFactory();
        else
            factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        
        // Avoid cipher suites with key lengths greater than 64 bits to stay within regulation
        factory = new SSLServerSocketFactoryProxy(factory);
        SSLServerSocket result = (SSLServerSocket) factory.createServerSocket(port);
        printServerSocketInfo(result);
        return result;
    }
    
    /*
     * @see za.co.softco.rest.RestServer#createClientSocket(java.net.InetAddress, int)
     */
    @Override
    protected Socket createClientSocket(InetAddress host, int port) throws IOException {
        if (host == null)
            host = InetAddress.getLocalHost();
        SSLSocketFactory factory;
        if (sslContext != null)
            factory = (SSLSocketFactory) sslContext.getSocketFactory();
        else
            factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        SSLSocket result = (SSLSocket) factory.createSocket(host, port);
        result.addHandshakeCompletedListener(new HandshakeCompletedListener() {
            @Override
            public void handshakeCompleted(HandshakeCompletedEvent event) {
                log.info("Client: Handshake completed");
            }
        });
        printSocketInfo(result);
        return result;
    }
    
    /*
     * @see za.co.softco.rest.RestServer#verifyConnection(java.net.Socket)
     */
    @Override
    protected Socket verifyConnection(Socket socket) throws IOException {
        SSLSocket ssl = (SSLSocket) socket;
        ssl.addHandshakeCompletedListener(new HandshakeCompletedListener() {
            @Override
            public void handshakeCompleted(HandshakeCompletedEvent event) {
                log.info("Server: Handshake completed");
            }
        });
        return ssl;
    }

    /**
     * Print socket information
     * @param s
     */
    private static void printSocketInfo(SSLSocket s) {
        System.out.println("Socket class: " + s.getClass());
        System.out.println("   Remote address = " + s.getInetAddress().toString());
        System.out.println("   Remote port = " + s.getPort());
        System.out.println("   Local socket address = " + s.getLocalSocketAddress().toString());
        System.out.println("   Local address = " + s.getLocalAddress().toString());
        System.out.println("   Local port = " + s.getLocalPort());
        System.out.println("   Need client authentication = " + s.getNeedClientAuth());
        SSLSession ss = s.getSession();
        System.out.println("   Cipher suite = " + ss.getCipherSuite());
        System.out.println("   Protocol = " + ss.getProtocol());
    }

    /**
     * Print socket information
     * @param s
     */
    private static void printServerSocketInfo(SSLServerSocket s) {
        System.out.println("Server socket class: " + s.getClass());
        System.out.println("   Socket address = " + s.getInetAddress().toString());
        System.out.println("   Socket port = " + s.getLocalPort());
        System.out.println("   Need client authentication = " + s.getNeedClientAuth());
        System.out.println("   Want client authentication = " + s.getWantClientAuth());
        System.out.println("   Use client mode = " + s.getUseClientMode());
    }

    /**
     * Application main method
     * @param args
     */
    public static void main(String[] args) {
        int port = DEFAULT_SECURE_PORT;
        int minWorkers = 0;
        int maxWorkers = 0;
        if (args.length > 0)
            port = Integer.parseInt(args[0]);
        if (args.length > 1)
            minWorkers = Integer.parseInt(args[1]);
        if (args.length > 2)
            maxWorkers = Integer.parseInt(args[2]);
        try {
            new SecureRestServer(port, minWorkers, maxWorkers, false, false, Compression.NONE, null, new DummyRestHandler()).run();
            System.exit(0);
        } catch (Exception e) {
            System.exit(1);
        }
    }
    
    private class SSLServerSocketFactoryProxy extends SSLServerSocketFactory {

    	private final SSLServerSocketFactory delegate;
    	
    	public SSLServerSocketFactoryProxy(SSLServerSocketFactory delegate) {
    		this.delegate = delegate;
    	}
    	
		@Override
		public String[] getDefaultCipherSuites() {
			String[] tmp = delegate.getDefaultCipherSuites();
			List<String> result = new ArrayList<String>(tmp.length);
			for (String suite : tmp) {
				if (!suite.contains("128") && !suite.contains("256"))
					result.add(suite);
			}
			return result.toArray(new String[result.size()]);
		}

		@Override
		public String[] getSupportedCipherSuites() {
			String[] tmp = delegate.getSupportedCipherSuites();
			List<String> result = new ArrayList<String>(tmp.length);
			for (String suite : tmp) {
				if (!suite.contains("128") && !suite.contains("256"))
					result.add(suite);
			}
			return result.toArray(new String[result.size()]);
		}

		@Override
		public ServerSocket createServerSocket(int port) throws IOException {
			return delegate.createServerSocket(port);
		}

		@Override
		public ServerSocket createServerSocket(int port, int backlog) throws IOException {
			return delegate.createServerSocket(port, backlog);
		}

		@Override
		public ServerSocket createServerSocket(int port, int backlog, InetAddress ifaddress) throws IOException {
			return delegate.createServerSocket(port, backlog, ifaddress);
		}
    	
    }
}

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

import static za.co.softco.rest.http.HttpConstants.DEFAULT_PORT;
import static za.co.softco.rest.http.HttpConstants.HTTP_INTERNAL_ERROR;
import static za.co.softco.rest.http.HttpConstants.HTTP_UNAVAILABLE;
import static za.co.softco.rest.http.HttpConstants.RELEASE_SIGNAL;
import static za.co.softco.rest.http.HttpConstants.TERMINATE_SIGNAL;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.net.ssl.SSLException;

import org.apache.log4j.Logger;

import za.co.softco.rest.http.Compression;
import za.co.softco.rest.model.Context;
import za.co.softco.thread.Log4JDeadlockListener;
import za.co.softco.thread.ThreadDeadlockDetector;

/**
 * This class represents a REST server that listens for connections on
 * a specific port and manages client handlers.
 * @author john
 */
public class RestServer implements Runnable {

    private static final int DEADLOCK_CHECK_INTERVAL_MS = 60000;
    protected static final Logger log = Logger.getLogger(RestServer.class);

    /* Contexts used to get REST request inside a method */
    private static Map<Thread,Context> contexts = new HashMap<Thread,Context>();
    
    /* our server's configuration information is stored in these properties */
    // protected static Properties props = new Properties();

    /* Where worker threads stand idle */
    private final List<RestWorker> allWorkers = new LinkedList<RestWorker>();

    /* Where worker threads stand idle */
    private final List<RestWorker> workers = new LinkedList<RestWorker>();

    /* List of actions to call once service is terminated */
    private final List<Runnable> terminators = new LinkedList<Runnable>();
    
    /* the web server's virtual root */
    private final File root;

    /* server port */
    private final int serverPort;
    
    /* timeout on client connections */
    private final int timeout;

    private final RestFactory factory;

    /* Maximum number of worker threads */
    private final int maxWorkerThreads;

    /* Use one connection for more than one HTTP request */
    private final boolean keepConnection;

    /* Should content length be ignored? (Read until end of stream for POST body) */
    private final boolean ignoreContentLength;

    /* Default compression of result */
    private final Compression defaultCompression;
    
    /* Server socket */
    private ServerSocket serverSocket;

    /* Stays false until server is terminated */
    private boolean terminated = false;

    private final ActionListener listener = new ActionListener() {
        @Override
        public void actionPerformed(ActionEvent evt) {
            if (evt == null) {
                log.error("Listener called with no event");
                return;
            }
            // String command = evt.getActionCommand();
            // if (command != null)
            // log.debug(evt.getActionCommand());

            switch (evt.getID()) {
            case TERMINATE_SIGNAL:
                terminate();
                break;
            case RELEASE_SIGNAL:
                if (evt.getSource() instanceof RestWorker)
                    release((RestWorker) evt.getSource());
                else
                    log.error("Release event without a valid worker received");
                break;
            default:
                log.error("Unknown event: " + evt.getID() + " (" + evt.getActionCommand() + ")");
            }
        }
    };

    /**
     * Constructor
     * @param factory
     * @param serverPort
     * @param minWorkerThreads
     * @param maxWorkerThreads
     * @param ignoreContentLength
     * @param defaultCompression
     * @throws Exception
     */
    public RestServer(RestFactory factory, int serverPort, int minWorkerThreads, int maxWorkerThreads, boolean keepConnection, boolean ignoreContentLength, Compression defaultCompression) throws Exception {
        this.keepConnection = keepConnection;
        this.ignoreContentLength = ignoreContentLength;
        this.defaultCompression = defaultCompression;
        this.serverPort = serverPort;
        File f = new File(System.getProperty("java.home") + File.separator + "lib" + File.separator + "www-server.properties");

        Properties props = new Properties();
        if (f.exists()) {
            InputStream is = new BufferedInputStream(new FileInputStream(f));
            try {
                props.load(is);
            } finally {
                is.close();
            }
        }
        if (minWorkerThreads <= 0)
            minWorkerThreads = getInt(props, "minworkers", 5);
        if (minWorkerThreads <= 0)
            minWorkerThreads = getInt(props, "workers", 5);
        if (maxWorkerThreads <= 0)
            minWorkerThreads = getInt(props, "maxworkers", 0);

        this.maxWorkerThreads = maxWorkerThreads;

        if (serverPort <= 0)
            serverPort = getInt(props, "port", DEFAULT_PORT);

        timeout = getInt(props, "timeout", 10) * 1000;
        root = getVirtualRoot(props);

        if (factory instanceof DefaultRestFactory)
            ((DefaultRestFactory) factory).load(props);

        if (factory != null)
            this.factory = factory;
        else
            this.factory = new DefaultRestFactory(props);

        if (this.factory.getCount() == 0)
            throw new RestException(HTTP_INTERNAL_ERROR, "No REST handlers registered");

        printProps(serverPort);

        /* start worker threads */
        for (int i = 0; i < minWorkerThreads; ++i) {
            RestWorker w = new RestWorker(factory, root, getProtocol(), serverPort, this.timeout, keepConnection, ignoreContentLength, listener, defaultCompression);
            new Thread(w, "worker #" + i).start();
            allWorkers.add(w);
            workers.add(w);
        }

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread t, Throwable e) {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintWriter p = new PrintWriter(out);
                try {
                    p.println("Uncaught exception: " + e.getClass().getSimpleName() + " - " + e.getMessage());
                    p.println("Stack trace:");
                    e.printStackTrace(p);
                    p.flush();
                } finally {
                    p.close();
                }
                log.error("Stack trace:" + new String(out.toByteArray()));
            }
        });

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                PrintWriter p = new PrintWriter(out);
                p.println("System shutting down...");
                p.println("System exit stack traces:");
                try {
                    for (Map.Entry<Thread, StackTraceElement[]> trace : Thread.getAllStackTraces().entrySet()) {
                        p.println("Thread: " + trace.getKey().getName());
                        for (StackTraceElement el : trace.getValue())
                            p.println(" at " + el.toString());
                        p.println();
                    }
                    p.flush();
                } finally {
                    p.close();
                }
                log.info(new String(out.toByteArray()));
            }
        }));

        new ThreadDeadlockDetector(DEADLOCK_CHECK_INTERVAL_MS).addListener(new Log4JDeadlockListener());
    }

    /**
     * Constructor
     * @param serverPort
     * @param maxWorkerThreads
     * @param ignoreContentLength
     * @param defaultCompression
     * @param workerThreads
     * @param factory
     * @throws Exception
     */
    public RestServer(int serverPort, int minWorkerThreads, int maxWorkerThreads, boolean keepConnection, boolean ignoreContentLength, Compression defaultCompression, RestHandler... handlers) throws Exception {
        this(createFactory(handlers), serverPort, minWorkerThreads, maxWorkerThreads, keepConnection, ignoreContentLength, defaultCompression);
    }

    /**
     * Add a termination task. This task will be executed once the server has been terminated
     * @param onTerminate
     */
    public void addTerminateTask(Runnable onTerminate) {
        if (onTerminate != null)
            terminators.add(onTerminate);
    }
    
    /**
     * Remove a termination task
     * @param onTerminate
     */
    public void removeTerminateTask(Runnable onTerminate) {
        if (onTerminate != null)
            terminators.remove(onTerminate);
    }
    
    /**
     * Return the protocol used to build other URL's
     * @return
     */
    protected String getProtocol() {
        return "http";
    }
    
    /**
     * Create a server socket
     * @param port
     * @return
     * @throws IOException
     */
    protected ServerSocket createServerSocket(int port) throws Exception {
        return new ServerSocket(port);
    }
    
    /**
     * Open a connection to a host
     * @param host
     * @param port
     * @return
     * @throws IOException
     */
    protected Socket createClientSocket(InetAddress host, int port) throws IOException {
        if (host == null)
            host = InetAddress.getLocalHost();
        return new Socket(host, port);
    }
    
    /**
     * Verify that a socket connection made by a client is acceptable
     * @param socket
     * @return
     * @throws IOException
     */
    protected Socket verifyConnection(Socket socket) throws IOException {
        return socket;
    }

    /**
     * Create a REST factory
     * @param handlers
     * @return
     */
    protected static RestFactory createFactory(RestHandler... handlers) {
        RestFactory result = new DefaultRestFactory(null);
        for (RestHandler handler : handlers)
            result.register(handler.getClass().getName().replaceAll(".*\\.", ""), handler);
        return result;
    }

    /**
     * Terminate the server
     */
    public void terminate() {
        terminated = true;
        if (serverSocket == null)
            return;
        try {
            createClientSocket(InetAddress.getLocalHost(), serverSocket.getLocalPort()).close();
        } catch (UnknownHostException e) {
            log.warn("Unexpected error: " + e.getMessage());
        } catch (IOException e) {
            log.warn("Unexpected error: " + e.getMessage());
        }
    }

    /**
     * Returns true if service has been shut down
     * @return
     */
    public boolean isShutDown() {
        if (!terminated)
            return false;
        return (serverSocket == null) || serverSocket.isClosed();
    }
    
    /**
     * Release a worker
     * @param worker
     */
    public void release(RestWorker worker) {
        synchronized (workers) {
            if (!workers.contains(worker)) {
                // log.debug("Releasing worker");
                workers.add(worker);
            }
        }
    }

    /**
     * Write a server busy response to a socket
     * @param socket
     * @throws IOException
     */
    private void serverBusy(Socket socket) throws IOException {
        OutputStream out = socket.getOutputStream();
        RestWorker.writeErrorResponse(HTTP_UNAVAILABLE, "Server busy", out);
        out.flush();
        socket.close();
    }

    /*
     * @see java.lang.Runnable#run()
     */
    @Override
    public void run() {
        try {
            log.info("Opening server port...");
            this.serverSocket = createServerSocket(serverPort);
            log.info("Server port opened");
        } catch (Exception e) {
            log.error("Failed to create server port: " + e.getMessage(), e);
        }
        
        log.info("Server started");
        long lastError = 0;
        long sslLastError = 0;
        int errorCount = 0;
        int sslErrorCount = 0;
        try {
            try {
                while (serverSocket != null && !terminated) {
                    try {
                        Socket s = serverSocket.accept();
                        verifyConnection(s);

                        RestWorker w = null;
                        synchronized (workers) {
                            if (workers.isEmpty()) {
                                if (maxWorkerThreads > 0 && allWorkers.size() >= maxWorkerThreads) {
                                    serverBusy(s);
                                    continue;
                                }
                                s.setSoTimeout(timeout);
                                s.setTcpNoDelay(true);
                                log.warn("Creating addional worker");
                                RestWorker ws = new RestWorker(factory, root, getProtocol(), serverSocket.getLocalPort(), this.timeout, keepConnection, ignoreContentLength, listener, defaultCompression);
                                int no = allWorkers.size();
                                allWorkers.add(ws);
                                ws.setSocket(s);
                                (new Thread(ws, "worker #" + no)).start();
                            } else {
                                // log.debug("Removing worker");
                                w = workers.get(0);
                                workers.remove(0);
                                w.setSocket(s);
                            }
                        }
                    } catch (SSLException e) {
                        if (sslLastError == 0 || sslLastError < System.currentTimeMillis() - 5000)
                            sslErrorCount = 0;
                        sslErrorCount++;
                        if (sslErrorCount > 10)
                            throw e;
                    } catch (IOException e) {
                        if (lastError == 0 || lastError < System.currentTimeMillis() - 5000)
                            errorCount = 0;
                        errorCount++;
                        if (errorCount > 10)
                            throw e;
                    }
                }
                
                log.info("Server terminated");
                for (RestWorker worker : allWorkers)
                    worker.terminate();
            } finally {
            	if (serverSocket != null) {
	                serverSocket.close();
	                serverSocket = null;
	                log.info("Server socket closed");
            	}
                
                // Notifying listeners that service has terminated
                for (Runnable onTerminate : terminators)
                    onTerminate.run();
            }
        } catch (IOException e) {
            log.error("Server terminated", e);
        }
    }

    /**
     * Parse a property as an integer
     * @param props
     * @param name
     * @param defaultValue
     * @return
     */
    private static int getInt(Properties props, String name, int defaultValue) {
        String prop = props.getProperty(name);
        if (prop == null || prop.trim().length() == 0)
            return defaultValue;
        try {
            return Math.max(1, Integer.parseInt(prop));
        } catch (NumberFormatException e) {
            log.error("Could not parse \"" + prop + "\" as integer", e);
        }
        return defaultValue;

    }

    /**
     * Return the virtual file system root for downloading static files from
     * @param props
     * @return
     * @throws IOException
     */
    private static File getVirtualRoot(Properties props) throws IOException {
        String r = props.getProperty("root");
        if (r != null && r.trim().length() > 0) {
            File result = new File(r);
            if (!result.exists())
                throw new IOException(result + " doesn't exist as server root");
            return result;
        }
        return new File(System.getProperty("user.dir"));
    }

    /**
     * Print all properties
     * @param port
     */
    private void printProps(int port) {
        log.info("root=" + root);
        log.info("timeout=" + timeout);
        log.info("workers=" + allWorkers);
        log.info("port=" + port);
    }

    /**
     * Startup method
     * @param args
     */
    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        int minWorkers = 0;
        int maxWorkers = 0;
        if (args.length > 0)
            port = Integer.parseInt(args[0]);
        if (args.length > 1)
            minWorkers = Integer.parseInt(args[1]);
        if (args.length > 2)
            maxWorkers = Integer.parseInt(args[2]);
        try {
            new RestServer(port, minWorkers, maxWorkers, false, false, null, new DummyRestHandler()).run();
            System.exit(0);
        } catch (Exception e) {
            System.exit(1);
        }
    }
    
    static void setContext(Context context) {
    	contexts.put(Thread.currentThread(), context);
    }
    
    static void resetContext() {
    	contexts.remove(Thread.currentThread());
    }
    
    /**
     * Used by service classes to get the current context
     * @return
     */
    public static Context getContext() {
    	Context result = contexts.get(Thread.currentThread());
    	if (result == null)
    		throw new IllegalStateException("Attempt to get context from non handler thread");
    	return result;
    }
}

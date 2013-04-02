/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 23 Jun 2010
 *******************************************************************************/
package example;

import static org.tanukisoftware.wrapper.WrapperManager.WRAPPER_CTRL_CLOSE_EVENT;
import static org.tanukisoftware.wrapper.WrapperManager.WRAPPER_CTRL_C_EVENT;
import static org.tanukisoftware.wrapper.WrapperManager.WRAPPER_CTRL_HUP_EVENT;
import static org.tanukisoftware.wrapper.WrapperManager.WRAPPER_CTRL_LOGOFF_EVENT;
import static org.tanukisoftware.wrapper.WrapperManager.WRAPPER_CTRL_SHUTDOWN_EVENT;
import static org.tanukisoftware.wrapper.WrapperManager.WRAPPER_CTRL_TERM_EVENT;

import java.io.IOException;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.tanukisoftware.wrapper.WrapperListener;
import org.tanukisoftware.wrapper.WrapperManager;

import za.co.softco.rest.DefaultRestFactory;
import za.co.softco.rest.ReflectionHandler;
import za.co.softco.rest.ReflectionService;
import za.co.softco.rest.RestFactory;
import za.co.softco.rest.RestServer;
import za.co.softco.rest.http.Compression;

/**
 * This is an example which uses the Tanuki Software Java Service Wrapper to
 * create a platform independent service. All you need to get going is to
 * make a copy of this class and change the first four constants to your
 * liking.
 * 
 * If your application needs multiple handler classes, have a look at the code
 * where the HANDLER constant is used - you can add more handlers there.
 * 
 * If your startup takes long, you must repeatedly signal the service wrapper
 * with SIG_START_WAIT_MS in order for the system not to abort starting up.
 * 
 * @author john
 */
public class RestService implements WrapperListener {

    /** An instance of the class that will be handling your requests */
    private static final Object HANDLER = new GreetHandler();
    
    /** The root path of your URL (e.g. http://localhost:8088/rest) */
    public static final String SERVICE_ROOT = "rest";

    /** The path used to access a specific handler (e.g. http://localhost:8088/rest/greet) */
    public static final String GREET_SERVICE = "greet";
    
    /** The port on which your service listens */
    public static final int SERVICE_PORT  = 8088;

    /** Number of worker threads created on startup */
    public static final int MIN_WORKERS = 2;
    
    /** The maximum number of worker threads allowed */
    public static final int MAX_WORKERS = 5;
    
    
    // Some other constants used by the service
    public static final int SIG_START_WAIT_MS           = 5000;
    public static final int SERVER_ALREADY_STARTED      = 1;
    public static final int ERROR_IO_ERROR              = 21;
    public static final int ERROR_CONFIGURATION_ERROR   = 31;
    public static final int ERROR_TIMEOUT_ERROR         = 41;
    public static final int ERROR_INTERRUPTED           = 51;
    public static final int ERROR_SHUTDOWN_FAILED       = 1;
    public static final int ERROR_SHUTDOWN_INTERRUPTED  = 2;
    
    protected static final Logger logger = Logger.getLogger(RestService.class.getName());

    private boolean stopping = false;
    private RestServer server;
    
    /*
     * @see org.tanukisoftware.wrapper.WrapperListener#controlEvent(int)
     */
    @Override
    public void controlEvent(int event) {
        logger.info("Event received: " + event);
        switch (event) {
        case WRAPPER_CTRL_C_EVENT :
        case WRAPPER_CTRL_SHUTDOWN_EVENT :
        case WRAPPER_CTRL_HUP_EVENT :        
        case WRAPPER_CTRL_TERM_EVENT :
            stop(event);
            break;
        case WRAPPER_CTRL_CLOSE_EVENT :
        case WRAPPER_CTRL_LOGOFF_EVENT :
        }
    }

    /*
     * @see org.tanukisoftware.wrapper.WrapperListener#start(java.lang.String[])
     */
    @Override
    public Integer start(String[] args) {
        if (server != null) {
            logger.error("ReportServer already started");
            return Integer.valueOf(SERVER_ALREADY_STARTED);
        }
        WrapperManager.signalStarting(SIG_START_WAIT_MS);
        BasicConfigurator.configure();
        
        Runtime.getRuntime().addShutdownHook(new Thread() {
        @Override
        public void run() {
               logger.info("Shutting down...");
               RestService.this.stop(0);
               logger.info("Shutdown completed.");
           }
        });
        
        System.out.println("Starting service...");
        WrapperManager.signalStarting(SIG_START_WAIT_MS);
        
        try {
            int port = SERVICE_PORT;
            int minWorkers = MIN_WORKERS;
            int maxWorkers = MAX_WORKERS;
            Compression compression = Compression.NONE;
            
            RestFactory factory = new DefaultRestFactory();
            ReflectionHandler handler = new ReflectionHandler(SERVICE_ROOT);
            factory.register(handler);
            handler.register(new ReflectionService(GREET_SERVICE, HANDLER));
            server = new RestServer(factory, port, minWorkers, maxWorkers, false, false, compression);
            server.addTerminateTask(new Runnable() {
                @SuppressWarnings("synthetic-access")
                @Override
                public void run() {
                    if (!stopping)
                        WrapperManager.stop(0);
                } 
            });
            
            Thread thread = new Thread(server);
            thread.setDaemon(true);
            thread.start();
            logger.info("Service started successfully.");
            return null;
        } catch (IOException e) {
            logger.error("IO error: " + e.getMessage(), e);
            logger.trace("IO error", e);
            return new Integer(ERROR_IO_ERROR);
        } catch (Exception e) {
            logger.error("Error: " + e.getMessage(), e);
            logger.trace("Error", e);
            return new Integer(ERROR_CONFIGURATION_ERROR);
        }
    }

    /* 
     * @see org.tanukisoftware.wrapper.WrapperListener#stop(int)
     */
    @Override
    public int stop(int exitCode) {
        stopping = true;
        try {
            logger.info("Stopping service...");
            if (this.server != null)
                this.server.terminate();
            
            long timeout = System.currentTimeMillis() + 5000;
            try {
                do {
                    if (this.server.isShutDown()) {
                        WrapperManager.signalStopped(exitCode);
                        return exitCode;
                    }
                    Thread.sleep(500);
                } while (System.currentTimeMillis() < timeout);
            } catch (InterruptedException e) {
                exitCode = ERROR_SHUTDOWN_INTERRUPTED;
                WrapperManager.signalStopped(exitCode);
                return exitCode;
            }
            exitCode = ERROR_SHUTDOWN_FAILED;
            WrapperManager.signalStopped(exitCode);
            return exitCode;
        } finally {
            stopping = false;
        }
    }

    /**
     * This main method allows the service to be started as a normal Java application  
     * @param args
     * @throws IOException 
     */
    public static void main(String[] args)  {
        System.out.println("Starting service as application");
        final RestService instance = new RestService();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                instance.stop(0);
            }
        }));
        logger.info("Before WrapperManager.start()");
        WrapperManager.start(instance, args);
        logger.info("After WrapperManager.start()");
    }
}

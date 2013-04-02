/*******************************************************************************
 *              Copyright (C) Bester Consulting 2010. All Rights reserved.
 * @author      John Bester
 * Project:     SoftcoRest
 * Description: HTTP REST Server
 *
 * Changelog  
 *  $Log$
 *  Created on 15 Oct 2010
 *******************************************************************************/
package za.co.softco.thread;

import java.io.BufferedWriter;
import java.io.StringWriter;
import java.text.NumberFormat;

import org.apache.log4j.Logger;


/**
 * @author john
 * 
 */
public class Log4JDeadlockListener implements DeadlockListener {
    private final Logger log = Logger.getLogger(Log4JDeadlockListener.class);
    private final NumberFormat pcFormat;
    
    public Log4JDeadlockListener() {
        pcFormat = NumberFormat.getInstance();
        pcFormat.setMaximumFractionDigits(1);
        pcFormat.setMinimumFractionDigits(0);
    }
    
    @Override
    public void deadlockDetected(Thread[] threads) {
        try {
            StringWriter text = new StringWriter();
            BufferedWriter out = new BufferedWriter(text);
            try {
                out.write("Deadlocked Threads:\r\n");
                out.newLine();
                for (Thread thread : threads) {
                    out.write("Thread: " + thread);
                    out.newLine();
                    for (StackTraceElement ste : thread.getStackTrace()) {
                        out.write("\t" + ste);
                        out.newLine();
                    }
                    out.newLine();
                }
            } finally {
                out.flush();
                out.close();
            }
            log.error(text.toString());
        } catch (Exception e) {
            log.error("Failed to log deadlock exception", e);
        }
    }

    @Override
    public void memoryUsageChanged(double freeMemoryPC, long freeMemory) {
        log.info("Memory usage changed: " + pcFormat.format(freeMemoryPC) + "%  (" + freeMemory + " bytes free)");
    }

    @Override
    public void memoryUsageCritical(double freeMemoryPC, long freeMemory) {
        log.warn("Critical memory usage: " + pcFormat.format(freeMemoryPC) + "%  (" + freeMemory + " bytes free)");
    }
}

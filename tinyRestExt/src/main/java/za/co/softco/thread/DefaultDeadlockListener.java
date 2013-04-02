/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: DefaultDeadlocakListener.java,v $
 *  Created on 01-Dec-2005
 *******************************************************************************/
package za.co.softco.thread;

import java.text.NumberFormat;

public class DefaultDeadlockListener implements DeadlockListener {
    private final NumberFormat pcFormat;
    
    public DefaultDeadlockListener() {
        pcFormat = NumberFormat.getInstance();
        pcFormat.setMaximumFractionDigits(1);
        pcFormat.setMinimumFractionDigits(0);
    }
    
    @Override
    public void deadlockDetected(Thread[] threads) {
        System.err.println("Deadlocked Threads:");
        System.err.println("-------------------");
        for (Thread thread : threads) {
            System.err.println(thread);
            for (StackTraceElement ste : thread.getStackTrace()) {
                System.err.println("\t" + ste);
            }
        }
    }
    
    @Override
    public void memoryUsageChanged(double freeMemoryPC, long freeMemory) {
        System.out.println("Memory usage changed: " + pcFormat.format(freeMemoryPC) + "%  (" + freeMemory + " bytes free)");
    }
    
    @Override
    public void memoryUsageCritical(double freeMemoryPC, long freeMemory) {
        System.err.println("Critical memory usage: " + pcFormat.format(freeMemoryPC) + "%  (" + freeMemory + " bytes free)");
    }
}
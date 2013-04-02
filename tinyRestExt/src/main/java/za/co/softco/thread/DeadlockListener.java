/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: DeadlocakListener.java,v $
 *  Created on 01-Dec-2005
 *******************************************************************************/
package za.co.softco.thread;

/**
 * This is called whenever a problem with threads is detected.
 */
public interface DeadlockListener {
    public void deadlockDetected(Thread[] deadlockedThreads);
    public void memoryUsageChanged(double freeMemoryPC, long freeMemory);
    public void memoryUsageCritical(double freeMemoryPC, long freeMemory);
}
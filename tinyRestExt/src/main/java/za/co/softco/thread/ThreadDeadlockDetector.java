/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 15 Oct 2010
 *******************************************************************************/
package za.co.softco.thread;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author john
 * 
 */
public class ThreadDeadlockDetector {
    private final Timer threadCheck = new Timer("ThreadDeadlockDetector", true);
    private final ThreadMXBean mbean = ManagementFactory.getThreadMXBean();
    private final Collection<DeadlockListener> listeners = new CopyOnWriteArraySet<DeadlockListener>();
    private double memoryUsage = 0;

    /**
     * The number of milliseconds between checking for deadlocks. It may be expensive to check for deadlocks, and it is not critical to know so
     * quickly.
     */
    private static final int DEFAULT_DEADLOCK_CHECK_PERIOD = 10000;

    public ThreadDeadlockDetector() {
        this(DEFAULT_DEADLOCK_CHECK_PERIOD);
    }

    public ThreadDeadlockDetector(int deadlockCheckPeriod) {
        threadCheck.schedule(new TimerTask() {
            @Override
            @SuppressWarnings("synthetic-access")
            public void run() {
                checkForDeadlocks();
            }
        }, 10, deadlockCheckPeriod);
    }

    private void checkForDeadlocks() {
        double total = Runtime.getRuntime().totalMemory();
        if (total > 0) {
            long free = Runtime.getRuntime().freeMemory();
            double memory = free / total;
            int diff = (int) Math.abs(memory - this.memoryUsage);
            if (memory > 90) {
                if (diff >= 1) {
                    fireMemoryUsageChanged(memory, free);
                    this.memoryUsage = memory;
                }
            } else if (memory > 80) {
                if (diff >= 2) {
                    fireMemoryUsageChanged(memory, free);
                    this.memoryUsage = memory;
                }
            } else if (diff >= 5) {
                fireMemoryUsageChanged(memory, free);
                this.memoryUsage = memory;
            }
        }
        long[] ids = findDeadlockedThreads();
        if (ids != null && ids.length > 0) {
            Thread[] threads = new Thread[ids.length];
            for (int i = 0; i < threads.length; i++) {
                threads[i] = findMatchingThread(mbean.getThreadInfo(ids[i]));
            }
            fireDeadlockDetected(threads);
        }
    }

    private long[] findDeadlockedThreads() {
        // JDK 1.5 only supports the findMonitorDeadlockedThreads()
        // method, so you need to comment out the following three lines
        if (mbean.isSynchronizerUsageSupported())
            return mbean.findDeadlockedThreads();
        else
            return mbean.findMonitorDeadlockedThreads();
    }

    private void fireDeadlockDetected(Thread[] threads) {
        for (DeadlockListener l : listeners) {
            l.deadlockDetected(threads);
        }
    }

    private void fireMemoryUsageChanged(double freeMemoryPC, long freeMemory) {
        for (DeadlockListener l : listeners) {
            if (freeMemoryPC >= 90)
                l.memoryUsageCritical(freeMemoryPC, freeMemory);
            else
                l.memoryUsageChanged(freeMemoryPC, freeMemory);
        }
    }

    private Thread findMatchingThread(ThreadInfo inf) {
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (thread.getId() == inf.getThreadId()) {
                return thread;
            }
        }
        throw new IllegalStateException("Deadlocked Thread not found");
    }

    public boolean addListener(DeadlockListener l) {
        return listeners.add(l);
    }

    public boolean removeListener(DeadlockListener l) {
        return listeners.remove(l);
    }
}

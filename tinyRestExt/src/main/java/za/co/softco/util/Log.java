/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author      John Bester
 * Project:     Library 
 * Description: Library classes
 *
 * Changelog  
 *  $Log: Log.java,v $
 *  Revision 1.2  2007/12/22 19:37:34  remjohn
 *  Update classes to reflect latest functionality in Bester package
 *
 *  Revision 1.1  2007/08/15 13:05:58  rembrink
 *  Added to CVS
 *
 *  Revision 1.1  2007/08/05 08:31:11  john
 *  Converted base package to za.co.softco
 *
 *  Revision 1.1  2007/06/14 10:21:24  goofyxp
 *  Split besterBase from bester library
 *
 *  Revision 1.8  2006/12/28 10:23:06  obelix
 *  Dereference InvocationTargetException
 *
 *  Revision 1.7  2006/11/28 13:06:19  obelix
 *  Fix potential null pointer bug
 *
 *  Revision 1.6  2006/11/28 10:54:40  goofyxp
 *  Make getStackTrace() an instance function
 *
 *  Revision 1.5  2006/09/13 08:50:05  obelix
 *  Print stack trace when "verbose=true"
 *
 *  Revision 1.4  2006/06/16 15:06:22  obelix
 *  Changed get/setVerbose() to get/setDebugMode()
 *
 *  Revision 1.3  2006/06/14 08:35:42  goofyxp
 *  Renamed log functions to use the commons logging standards
 *
 *  Created on 2004
 *******************************************************************************/
package za.co.softco.util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.UndeclaredThrowableException;

import javax.script.ScriptException;

/**
 * This class has static methods that makes writing of log information easy
 * @author John
 * @version 1.0
 */
public class Log {
    private static final DynamicClass<Throwable> WRAPPED_EXCEPTION = new DynamicClass<Throwable>("org.mozilla.javascript.WrappedException", "sun.org.mozilla.javascript.WrappedException", "sun.org.mozilla.javascript.internal.WrappedException");
    private static final DynamicClass<Throwable> RHINO_EXCEPTIONS = new DynamicClass<Throwable>("org.mozilla.javascript.RhinoException", "sun.org.mozilla.javascript.RhinoException", "sun.org.mozilla.javascript.internal.RhinoException");
    private static final DynamicClass<Throwable> BSH_EVAL_EXCEPTIONS = new DynamicClass<Throwable>("bsh.EvalError");
    private static final DynamicClass<Throwable> BSH_TARGET_EXCEPTIONS = new DynamicClass<Throwable>("bsh.TargetError");

    /**
     * Determines whether a class in an internal library class
     * @param call
     * @return
     */
    protected static boolean internalIsSystemClass(StackTraceElement call) {
        String name = call.getClassName();
        if (name.equals(Log.class.getName()))
            return true;
        if (name.startsWith("java."))
            return true;
        if (name.startsWith("javax."))
            return true;
        if (name.startsWith("sun."))
            return true;
        if (name.startsWith("com."))
            return true;
        if (name.startsWith("net."))
            return true;
        if (name.startsWith("org."))
            return true;
        if (name.startsWith("sunw."))
            return true;
        return false;
    }

    /**
     * Determines whether a class in an internal library class
     * @param call
     * @return
     */
    private static boolean isSystemClass(StackTraceElement call) {
        return internalIsSystemClass(call);
    }

    /**
     * Remove internal calls from a stack trace
     * @param call
     * @return
     */
    public static StackTraceElement[] purgeStackTrace(StackTraceElement[] trace) {
        int i;
        for (i = 1; i < trace.length; i++)
            if (!isSystemClass(trace[i]))
                break;

        if (i < trace.length) {
            StackTraceElement[] result = new StackTraceElement[trace.length - i];
            for (int j = i; j < trace.length; j++)
                result[j - i] = trace[j];
            return result;
        }
        return trace;
    }

    /**
     * Get the original exception
     * @param error
     * @return
     */
    public static Throwable getOriginalException(Throwable error) {
        if (error instanceof UndeclaredThrowableException) {
            Throwable cause = ((UndeclaredThrowableException) error).getUndeclaredThrowable();
            if (cause != null)
                error = cause;
        }
        if (error instanceof InvocationTargetException) {
            Throwable cause = error.getCause();
            if (cause != null)
                error = cause;
        }
        return error;
    }
    
    /**
     * Get the best possible message from an exception
     * @param error
     * @return
     */
    public static String getMessage(Throwable error) {
        error = getOriginalException(error);
        String result = error.getMessage();
        if (result != null)
            return result;
        return error.getClass().getName();
    }

    /**
     * Convert a stack trace to text for logging purposes
     * @param error
     * @return
     */
    public static String stackTraceToText(Throwable error) {
        if (error == null)
            return null;
        try {
            StringWriter stackTrace = new StringWriter();
            try { 
                PrintWriter w = new PrintWriter(stackTrace);
                try {
                    error.printStackTrace(w);
                } finally {
                    w.close();
                }
                stackTrace.flush();
            } finally {
                stackTrace.close();
            }
            return stackTrace.toString();
        } catch (IOException e) {
            return "Failed to determine stack trace";
        }
    }
    
    /**
     * Unwrap an exception
     * @param error
     * @param level
     * @return
     */
    private static Throwable unwrap(Throwable error, int level) {
        if (error == null || level < 0)
            return error;
        Throwable result = error.getCause();
        try {
            if (error instanceof ScriptException)
                result = ((ScriptException) error).getCause();
            else if (WRAPPED_EXCEPTION.isInstance(error))
                result = (Throwable) WRAPPED_EXCEPTION.invoke(error, "getWrappedException");
            else if (RHINO_EXCEPTIONS.isInstance(error))
                result = new Exception((String) RHINO_EXCEPTIONS.invoke(error, "details"));
            else if (BSH_TARGET_EXCEPTIONS.isInstance(error))
                result = (Throwable) BSH_TARGET_EXCEPTIONS.invoke(error, "getTarget");
            else if (BSH_EVAL_EXCEPTIONS.isInstance(error)) 
                result = (Throwable) BSH_EVAL_EXCEPTIONS.invoke(error, "getCause");
            else if (error instanceof UndeclaredThrowableException)
                result = ((UndeclaredThrowableException) error).getUndeclaredThrowable();
            else if (error instanceof InvocationTargetException)
                result = ((InvocationTargetException) error).getTargetException();
            
            if (result == null || result == error)
                return error;
            return unwrap(result, --level);
        } catch (Exception e) {
            System.err.println("Could not unwrap exception of type " + error.getClass().getName());
            return error;
        }
    }
    
    public static Throwable unwrap(Throwable error) {
        return unwrap(error, 10);
    }
}

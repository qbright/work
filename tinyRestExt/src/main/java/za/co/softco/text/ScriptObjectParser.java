/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 28 Sep 2010
 *******************************************************************************/
package za.co.softco.text;

import za.co.softco.util.DynamicClass;

/**
 * @author john
 *
 */
public class ScriptObjectParser {

    private static final DynamicClass<Object> NATIVE_JAVA_OBJECT = new DynamicClass<Object>("org.mozilla.javascript.NativeJavaObject", "sun.org.mozilla.javascript.NativeJavaObject", "sun.org.mozilla.javascript.internal.NativeJavaObject");
    private static final DynamicClass<Object> NATIVE_JAVA_ARRAY = new DynamicClass<Object>("org.mozilla.javascript.NativeJavaArray", "sun.org.mozilla.javascript.NativeJavaArray", "sun.org.mozilla.javascript.internal.NativeJavaArray");
    private static final DynamicClass<Object> NATIVE_ARRAY = new DynamicClass<Object>("org.mozilla.javascript.NativeArray", "sun.org.mozilla.javascript.NativeArray", "sun.org.mozilla.javascript.internal.NativeArray");

    public static final Object unwrapNativeJavaObject(Object obj) {
        try {
            if (NATIVE_JAVA_OBJECT.isInstance(obj))
                return NATIVE_JAVA_OBJECT.invoke(obj, "unwrap");
        } catch (Throwable e) {
            // Ignore exception
        }
        
        return obj;
    }
    
    public static final Object unwrapNativeJavaArray(Object obj) {
        try {
            if (NATIVE_JAVA_ARRAY.isInstance(obj))
                return NATIVE_JAVA_ARRAY.invoke(obj, "unwrap");
        } catch (Throwable e) {
            // Ignore exception
        }

        return obj;
    }
    
    public static final Object unwrapNativeArray(Object obj) {
        try {
            if (NATIVE_ARRAY.isInstance(obj)) {
                int numItems = IntegerParser.toInt(NATIVE_ARRAY.invoke(obj, "getLength"));
                Object[] result = new Object[ numItems ];
                for (int i = 0; i < numItems; i++)
                    result[i] = NATIVE_ARRAY.invoke(obj, "get", Integer.valueOf(i), null);
                return result;
            }
        } catch (Throwable e) {
            // Ignore exception
        }

        return obj;
    }
    
    /**
     * Unwrap any type of java script object
     * @param obj
     * @return
     */
    public static final Object unwrap(Object obj) {
        Object result = unwrapNativeJavaObject(obj);
        result = unwrapNativeJavaArray(result);
        result = unwrapNativeArray(result);
        if (result == "undefined")
            result = null;
        return result;
    }
}

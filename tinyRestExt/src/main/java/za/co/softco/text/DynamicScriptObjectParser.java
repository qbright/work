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

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Date;

import org.apache.log4j.Logger;

/**
 * @author john
 *
 */
public class DynamicScriptObjectParser {

    private static final Class<?> SUN_NATIVE_JAVA_OBJECT;
    private static final Class<?> MOZ_NATIVE_JAVA_OBJECT;
    private static final Class<?> SUN_NATIVE_JAVA_ARRAY;
    private static final Class<?> MOZ_NATIVE_JAVA_ARRAY;
    private static final Class<?> SUN_NATIVE_DATE;
    private static final Class<?> MOZ_NATIVE_DATE;
    private static final Class<?> SUN_SCRIPTABLE; 
    private static final Class<?> MOZ_SCRIPTABLE; 
    
    private static Class<?> loadClassIfExists(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            Logger.getLogger(DynamicScriptObjectParser.class).warn("Class " + className + " not found");
            return null;
        }
    }
    
    static {
        SUN_NATIVE_JAVA_OBJECT = loadClassIfExists("sun.org.mozilla.javascript.NativeJavaObject");
        MOZ_NATIVE_JAVA_OBJECT = loadClassIfExists("sun.org.mozilla.javascript.NativeJavaObject");
        SUN_NATIVE_JAVA_ARRAY = loadClassIfExists("sun.org.mozilla.javascript.NativeJavaObject");
        MOZ_NATIVE_JAVA_ARRAY = loadClassIfExists("sun.org.mozilla.javascript.NativeJavaObject");
        SUN_NATIVE_DATE = loadClassIfExists("sun.org.mozilla.javascript.NativeDate");
        MOZ_NATIVE_DATE = loadClassIfExists("sun.org.mozilla.javascript.NativeDate");
        SUN_SCRIPTABLE = loadClassIfExists("sun.org.mozilla.javascript.Scriptable");
        MOZ_SCRIPTABLE = loadClassIfExists("org.mozilla.javascript.Scriptable");
    }
    
    private static final boolean isInstance(Class<?> clazz, Object obj) {
        if (clazz == null || obj == null)
            return false;
        return clazz.isInstance(obj);
    }
    
    private static final Method getMethod(Class<?> clazz, String name, Class<?>... parameterTypes) {
        try {
            return clazz.getMethod(name, parameterTypes);
        } catch (IllegalArgumentException e) {
            Logger.getLogger(DynamicScriptObjectParser.class).error(e);
            return null;
        } catch (SecurityException e) {
            Logger.getLogger(DynamicScriptObjectParser.class).error(e);
            return null;
        } catch (NoSuchMethodException e) {
            Logger.getLogger(DynamicScriptObjectParser.class).error(e);
            return null;
        }
    }
    
    private static final Object callMethod(Class<?> clazz, Object instance, String name, Object defaultResult) {
        try {
            Method m = getMethod(clazz, name);
            if (m != null)
                return m.invoke(instance);
            return defaultResult;
        } catch (IllegalArgumentException e) {
            Logger.getLogger(DynamicScriptObjectParser.class).error(e);
            return defaultResult;
        } catch (SecurityException e) {
            Logger.getLogger(DynamicScriptObjectParser.class).error(e);
            return defaultResult;
        } catch (IllegalAccessException e) {
            Logger.getLogger(DynamicScriptObjectParser.class).error(e);
            return defaultResult;
        } catch (InvocationTargetException e) {
            Logger.getLogger(DynamicScriptObjectParser.class).error(e);
            return defaultResult;
        }
    }
    
    private static final Object unwrap(Class<?> clazz, Object instance) {
        return callMethod(clazz, instance, "unwrap", instance);
    }

    private static final int getLength(Class<?> clazz, Object instance) {
        return IntegerParser.toInt(callMethod(clazz, instance, "getLength", Integer.valueOf(0)));
    }

    private static final Object get(Class<?> arrayClass, Class<?> scriptableClass, Object instance, int index) {
        Method m = null;
        if (scriptableClass != null) {
            m = getMethod(arrayClass, "get", int.class, scriptableClass);
            if (m == null)
                m = getMethod(arrayClass, "get", Integer.class, scriptableClass);
        }
        if (m == null) 
            m = getMethod(arrayClass, "get", int.class);
        if (m == null)
            m = getMethod(arrayClass, "get", Integer.class);
        if (m == null) 
            return null;
        try {
            if (m.getParameterTypes().length == 2)
                return m.invoke(instance, Integer.valueOf(index), null);
            return m.invoke(instance, Integer.valueOf(index));
        } catch (IllegalArgumentException e) {
            Logger.getLogger(DynamicScriptObjectParser.class).error(e);
            return null;
        } catch (IllegalAccessException e) {
            Logger.getLogger(DynamicScriptObjectParser.class).error(e);
            return null;
        } catch (InvocationTargetException e) {
            Logger.getLogger(DynamicScriptObjectParser.class).error(e);
            return null;
        }
    }

    private static Field getDateField(Class<?> clazz) {
        if (clazz == null)
            return null;
        for (Field fld : clazz.getDeclaredFields()) {
            if (fld.getName().equalsIgnoreCase("date")) {
                fld.setAccessible(true);
                return fld;
            }
        }
        return null;
    }
    
    private static Date castDate(Object obj) {
        try {
            Field f = getDateField(obj.getClass());
            if (f == null)
                return null;
            long val = LongParser.toLong(f.get(obj));
            return (val != 0 ? new Date(val) : null);
        } catch (Exception e) {
            Logger.getLogger(DynamicScriptObjectParser.class).error(e);
            return null;
        } 
    }
    
    static final Object unwrapNativeJavaObject(Object obj) {
        try {
            if (isInstance(MOZ_NATIVE_JAVA_OBJECT, obj))
                return unwrap(MOZ_NATIVE_JAVA_OBJECT, obj);
        } catch (Throwable e) {
            // Ignore exception
        }
        
        try {
            if (isInstance(SUN_NATIVE_JAVA_OBJECT, obj))
                return unwrap(SUN_NATIVE_JAVA_OBJECT, obj);
        } catch (Throwable e) {
            // Ignore exception
        }

        try {
            if (isInstance(MOZ_NATIVE_DATE, obj))
                return castDate(obj);
        } catch (Throwable e) {
            // Ignore exception
        }
        
        try {
            if (isInstance(SUN_NATIVE_DATE, obj))
                return castDate(obj);
        } catch (Throwable e) {
            // Ignore exception
        }

        return obj;
    }
    
    static final Object unwrapNativeJavaArray(Object obj) {
        try {
            if (isInstance(MOZ_NATIVE_JAVA_ARRAY, obj))
                return unwrap(MOZ_NATIVE_JAVA_ARRAY, obj);
        } catch (Throwable e) {
            // Ignore exception
        }
        try {
            if (isInstance(SUN_NATIVE_JAVA_ARRAY, obj))
                return unwrap(SUN_NATIVE_JAVA_ARRAY, obj);
        } catch (Throwable e) {
            // Ignore exception
        }

        return obj;
    }
    
    static final Object unwrapNativeArray(Object obj) {
        try {
            if (isInstance(MOZ_NATIVE_JAVA_ARRAY, obj)) {
                int numItems = getLength(MOZ_NATIVE_JAVA_ARRAY, obj);
                Object[] result = new Object[ numItems ];
                for (int i = 0; i < numItems; i++)
                    result[i] = get(MOZ_NATIVE_JAVA_ARRAY, MOZ_SCRIPTABLE, obj, i);
                return result;
            }
        } catch (Throwable e) {
            // Ignore exception
        }
        
        try {
            if (isInstance(SUN_NATIVE_JAVA_ARRAY, obj)) {
                int numItems = getLength(SUN_NATIVE_JAVA_ARRAY, obj);
                Object[] result = new Object[ numItems ];
                for (int i = 0; i < numItems; i++)
                    result[i] = get(SUN_NATIVE_JAVA_ARRAY, SUN_SCRIPTABLE, obj, i);
                return result;
            }
        } catch (Throwable e) {
            // Ignore exception
        }

        return obj;
    }
}

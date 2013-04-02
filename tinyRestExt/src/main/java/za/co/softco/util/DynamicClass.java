/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 17 Feb 2011
 *******************************************************************************/
package za.co.softco.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * @author john
 *
 */
public class DynamicClass<T> {

    private final Class<T>[] allowed;
    
    @SuppressWarnings("unchecked")
    public DynamicClass(String... allowedClassNames) {
        List<Class<T>> tmp = new LinkedList<Class<T>>();
        for (String className : allowedClassNames) {
            addClass(tmp, className);
        }
        allowed = tmp.toArray(new Class[tmp.size()]);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private void addClass(Collection<Class<T>> classes, String className) {
        try {
            classes.add((Class) Class.forName(className));
        } catch (ClassNotFoundException e) {
            return;
        }
    }
    
    /**
     * Return a class of which this object is an instance (if any are listed)
     * @param instance
     * @return
     */
    public Class<T> getClass(Object instance) {
        if (instance == null)
            return null;
        for (Class<T> c : allowed) {
            if (c.isInstance(instance))
                return c;
        }
        return null;
    }
    
    /**
     * Is an object an instance of one of the predefined classes?
     * @param instance
     * @return
     */
    public boolean isInstance(Object instance) {
    	if (instance instanceof Class<?>) 
    		return isClassInstance((Class<?>) instance);
        return (getClass(instance) != null);
    }

    /**
     * Is an object an instance of one of the predefined classes?
     * @param clazz
     * @return
     */
    public boolean isClassInstance(Class<?> clazz) {
    	if (clazz == null)
    		return false;
    	
    	for (Class<T> c : allowed) {
    		if (c.equals(clazz))
    			return true;
    	}

    	if (isClassInstance(clazz.getSuperclass()))
    		return true;
    	
    	for (Class<?> c : clazz.getInterfaces()) {
    		if (isClassInstance(c))
    			return true;
    	}
    	
        return false;
    }

    /**
     * Check whether a list of parameters can be used on a parameter list of a function
     * @param types
     * @param parameters
     * @return
     */
    private boolean compareParameters(Class<?>[] types, Object[] parameters) {
        if (types.length != parameters.length)
            return false;
        for (int i=0; i<types.length; i++) {
            if (types[i] == null)
                throw new IllegalArgumentException("Null parameter type is not acceptable");
            if (parameters[i] == null)
                continue;
            if (!types[i].isInstance(parameters[i]))
                return false;
        }
        return true;
    }
    
    /**
     * Invoke a static method
     * @param clazz
     * @param method
     * @param parameters
     * @return
     * @throws Exception
     */
    public Object invokeStatic(Class<T> clazz, String method, Object... parameters) throws Exception {
        if (clazz == null || method == null)
            throw new IllegalArgumentException("Clazz and method parameters are required");
        for (Method m : clazz.getMethods()) {
            if (!m.getName().equals(method))
                continue;
            if (!compareParameters(m.getParameterTypes(), parameters))
                continue;
            if ((m.getModifiers() & Modifier.STATIC) == 0)
                throw new IllegalArgumentException("Method is not static: " + m.toString());
            if ((m.getModifiers() & Modifier.PUBLIC) == 0)
                throw new IllegalArgumentException("Method is not public: " + m.toString());
            try {
                return m.invoke(null, parameters);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Access denied: " + m.toString());
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof Exception)
                    throw (Exception) e.getCause();
                throw Utils.cast(e, Exception.class);
            }
        }
        throw new IllegalArgumentException("Method not found: " + clazz.getName() + "." + method + "(...)");
    }
    
    /**
     * Invoke a method given a class
     * @param <X>
     * @param clazz
     * @param instance
     * @param method
     * @param parameters
     * @return
     * @throws Exception
     */
    public <X extends T> Object invoke(Class<T> clazz, X instance, String method, Object... parameters) throws Exception {
        if (instance == null)
            return invokeStatic(clazz, method, parameters);
        if (!clazz.isInstance(instance))
            throw new IllegalArgumentException(instance.getClass().getName() + " is not an instance of " + clazz.getName());
        if (clazz == null || method == null)
            throw new IllegalArgumentException("Clazz and method parameters are required");
        for (Method m : clazz.getMethods()) {
            if (!m.getName().equals(method))
                continue;
            if (!compareParameters(m.getParameterTypes(), parameters))
                continue;
            if ((m.getModifiers() & Modifier.PUBLIC) == 0)
                throw new IllegalArgumentException("Method is not public: " + m.toString());
            try {
                return m.invoke(instance, parameters);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("Access denied: " + m.toString());
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof Exception)
                    throw (Exception) e.getCause();
                throw Utils.cast(e, Exception.class);
            }
        }
        throw new IllegalArgumentException("Method not found: " + clazz.getName() + "." + method + "(...)");
    }

    /**
     * Invoke a method by first looking up a defined class of which the object is an instance
     * @param <X>
     * @param instance
     * @param method
     * @param parameters
     * @return
     * @throws Exception
     */
    public <X extends T> Object invoke(X instance, String method, Object... parameters) throws Exception {
        if (instance == null)
            return null;
        Class<T> clazz = getClass(instance);
        if (clazz == null)
            return null;
        return invoke(clazz, instance, method, parameters);
    }
    
    /**
     * Get a proxy instance
     * @param <X>
     * @param <P>
     * @param instance
     * @param interfc
     * @return
     */
    @SuppressWarnings("unchecked")
    public <X extends T,P> X getProxy(final X instance, final Class<P> interfc) {
        if (instance == null)
            return null;
        final Class<T> clazz = getClass(instance);
        if (clazz == null)
            return null;
        return (X) Proxy.newProxyInstance(null, new Class[] { interfc }, new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                return method.invoke(proxy, args);
            }
        }); 
    }
}

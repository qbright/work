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
 *  Created on 20 Nov 2009
 *******************************************************************************/
package za.co.softco.rest;

import static za.co.softco.rest.http.HttpConstants.HTTP_BAD_REQUEST;
import static za.co.softco.rest.http.HttpConstants.HTTP_INTERNAL_ERROR;
import static za.co.softco.rest.http.HttpConstants.HTTP_UNAUTHORIZED;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import za.co.softco.rest.model.Context;
import za.co.softco.text.DataParser;

/**
 * This class represents a REST service which is build from public functions
 * in a class.
 * @author john
 */
public class ReflectionService {

	public static final String ARGUMENT_REQUEST = new String("&");
	private static final String[] NO_ARGS = {};
	private static final String[] REQUEST_ARG_ONLY = { ARGUMENT_REQUEST };

	private final String serviceName;
	private final Class<?> implementation;
	private final Object instance;
	private final Map<String,Method> methods = new HashMap<String,Method>(); 
	private final Map<Method,String[]> parameterList = new HashMap<Method,String[]>(); 
	
	/**
	 * Constructor
	 * @param serviceName
	 * @param implementation
	 * @param instance
	 */
	public <T> ReflectionService(String serviceName, Class<T> implementation, T instance) {
		if (serviceName == null)
			serviceName = "";
		if (serviceName.startsWith("/"))
			serviceName = serviceName.substring(1).trim();
		this.serviceName = serviceName;
		this.implementation = implementation;
		this.instance = instance;
		if (implementation == null)
			throw new IllegalArgumentException("Implementation is required");
		try {
			implementation.newInstance();
		} catch (InstantiationException e) {
			throw new IllegalArgumentException("Service class cannot be instatiated: " + e.getMessage());
		} catch (IllegalAccessException e) {
			throw new IllegalArgumentException("Service class default constructor not accessible: " + e.getMessage());
		}
		autoRegisterMethods();
	}
	
	/**
	 * Constructor
	 * @param serviceName
	 * @param instance
	 */
	public ReflectionService(String serviceName, Object instance) {
		if (serviceName == null)
			serviceName = "";
		if (serviceName.startsWith("/"))
			serviceName = serviceName.substring(1).trim();
		this.serviceName = serviceName;
		if (instance instanceof Class<?>) {
			this.implementation = (Class<?>) instance;
			this.instance = null;
			try {
				implementation.newInstance();
			} catch (InstantiationException e) {
				throw new IllegalArgumentException("Service class cannot be instatiated: " + e.getMessage());
			} catch (IllegalAccessException e) {
				throw new IllegalArgumentException("Service class default constructor not accessible: " + e.getMessage());
			}
		} else {
			this.implementation = instance.getClass();
			this.instance = instance;
		}
		if (implementation == null)
			throw new IllegalArgumentException("Implementation is required");
		autoRegisterMethods();
	}

	/**
	 * Register methods by using reflection
	 */
	private void autoRegisterMethods() {
		for (Method m : implementation.getMethods()) {
			if ((m.getModifiers() & Modifier.PUBLIC) == 0)
				continue;
			Class<?>[] types = m.getParameterTypes();
			if (types.length == 0) {
				methods.put(m.getName(), m);
				parameterList.put(m, NO_ARGS);
			} else if (types.length == 1) { 
				if (types[0].equals(Context.class)) {
					methods.put(m.getName(), m);
					parameterList.put(m, REQUEST_ARG_ONLY);
				} else if (types[0].equals(RestRequest.class)) {
					methods.put(m.getName(), m);
					parameterList.put(m, REQUEST_ARG_ONLY);
				}
			}
		}
	}

	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return serviceName.hashCode();
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object ref) {
		if (!(ref instanceof ReflectionService))
			return false;
		return ((ReflectionService) ref).serviceName.equals(serviceName);
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return serviceName;
	}

	/**
	 * Register a REST method
	 * @param methodName
	 * @param argumentNames
	 */
	public void registerMethod(String methodName, String... argumentNames) {
		if (methodName == null)
			throw new IllegalArgumentException("Argument method invalid");
		methodName = methodName.trim();
		if (methodName.length() == 0)
			throw new IllegalArgumentException("Argument method invalid");
		
		if (argumentNames == null)
			argumentNames = new String[0];
		
		Set<String> unique = new HashSet<String>();
		for (int i=0; i<argumentNames.length; i++) {
			if (argumentNames[i] == null)
				throw new IllegalArgumentException("Argument name invalid");
			argumentNames[i] = argumentNames[i].trim().toLowerCase();
			if (argumentNames[i].length() == 0)
				throw new IllegalArgumentException("Argument name invalid");
			if (unique.contains(argumentNames[i]))
				throw new IllegalArgumentException("Duplicate argument name: " + argumentNames[i]);
			unique.add(argumentNames[i]);
		}
		
		Method method = null;
		String[] finalArgumentNames = null;
		methodLoop:
		for (Method m : implementation.getMethods()) {
			if ((m.getModifiers() & Modifier.PUBLIC) == 0)
				continue;
			if (m.getName().equals(methodName) && m.getParameterTypes().length >= argumentNames.length) {
				if (method != null)
					throw new IllegalArgumentException("Method overloading with the same number of arguments: " + method.toString());
				
				int requestCount = 0;
				List<String> tmpArgs = new LinkedList<String>();
				int pndx = 0;
				for (Class<?> type : m.getParameterTypes()) {
					if (argumentNames[pndx] == ARGUMENT_REQUEST) {
						tmpArgs.add(argumentNames[pndx++]);
					} else if (type.equals(RestRequest.class)) {
						requestCount++;
						tmpArgs.add(ARGUMENT_REQUEST);
					} else {
						if (pndx >= argumentNames.length)
							continue methodLoop;
						tmpArgs.add(argumentNames[pndx++]);
					}
				}
				
				if (tmpArgs.size() != m.getParameterTypes().length) 
					throw new IllegalStateException("Parameter list name calculation failed");
				
				if (m.getParameterTypes().length == argumentNames.length + requestCount) {
					finalArgumentNames = tmpArgs.toArray(new String[tmpArgs.size()]);
					method = m;
				}
			}
		}
		if (method == null)
			throw new IllegalArgumentException("Method " + methodName + " not found in " + implementation.getClass().getName());
		methods.put(methodName, method);
		if (finalArgumentNames == null || finalArgumentNames.length != method.getParameterTypes().length)
			throw new IllegalStateException("Parameter list name calculation failed");
		parameterList.put(method, finalArgumentNames);
	}
	
	/**
	 * Return a method by looking it up by name
	 * @param name
	 * @return
	 * @throws RestException
	 */
	public Method getMethod(String name) throws RestException {
		Method result = methods.get(name);
		if (result == null)
			throw new RestException(HTTP_BAD_REQUEST, "Method /" + serviceName + "/" + name +" not found");
		return result;
	}
	
	/**
     * Build a parameter list
	 * @param method
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public Object[] buildParameters(Method method, Context request) throws Exception {
		if (method == null)
			throw new IllegalArgumentException("Method is required");

		Class<?>[] types = method.getParameterTypes();
		if (types == null || types.length == 0)
			return new Object[0];
		
		if (types.length == 1 && types[0].equals(RestRequest.class))
			return new Object[] { request };
		
		String[] args = parameterList.get(method);
		Map<String,Object> params = new HashMap<String,Object>(); 
		String query = request.getURL().getQuery();
		if (query != null) {
			int pos = 0;
			while (pos >= 0 && pos < query.length()) {
				int next = query.indexOf('&', pos);
				if (next < 0)
					next = query.length();
				String param = query.substring(pos, next);
				int ndx = param.indexOf('=');
				if (ndx < 0) {
					params.put(param.trim().toLowerCase(), Boolean.TRUE);
				} else {
					String key = param.substring(0, ndx).trim().toLowerCase();
					String val = param.substring(ndx+1).trim();
					params.put(key, val);
				}
				pos = next+1;
			}
		}

		Object[] result = new Object[types.length];
		if (args == null || args.length > types.length)
			throw new IllegalStateException("Argument lists does not match");
		for (int i=0; i<types.length; i++) {
			if (types[i].equals(RestRequest.class) || args[i].equals(ARGUMENT_REQUEST)) 
				result[i] = request;
			else
				result[i] = DataParser.cast(params.get(args[i]), types[i]);
		}
		return result;
	}

	/**
	 * Handle a REST request
	 * @param request
	 * @param methodName
	 * @throws RestException
	 */
	public void handle(Context request, String methodName) throws RestException {
		if (methodName == null) 
			throw new IllegalStateException("Method name is required");
		
		Method method = getMethod(methodName);
		if (method == null)
			throw new RestException(HTTP_BAD_REQUEST, "Service method not found");
		
		try {
			Object[] args = buildParameters(method, request);
			if (instance != null)
				method.invoke(instance, args);
			else
				method.invoke(implementation.newInstance(), args);
		} catch (IllegalArgumentException e) {
			throw new RestException(HTTP_BAD_REQUEST, "Illegal argument: " + e.getMessage(), e);
		} catch (IllegalAccessException e) {
			throw new RestException(HTTP_INTERNAL_ERROR, e.getMessage(), e);
		} catch (InvocationTargetException e) {
		    Throwable target = e.getTargetException();
		    if (target instanceof RestException)
		        throw (RestException) target;
            if (target instanceof SecurityException)
                throw new RestException(HTTP_UNAUTHORIZED, e.getMessage());
			throw new RestException(HTTP_INTERNAL_ERROR, target);
		} catch (Exception e) {
			throw new RestException(HTTP_INTERNAL_ERROR, e.getMessage(), e);
		}
	}
}

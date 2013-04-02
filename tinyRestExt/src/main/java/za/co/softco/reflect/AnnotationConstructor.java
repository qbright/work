/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 14 Jan 2011
 *******************************************************************************/
package za.co.softco.reflect;

import java.beans.IntrospectionException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import za.co.softco.text.DataParser;
import za.co.softco.util.Utils;

/**
 * This class is used to instantiate a class by looking for constructors with 
 * annotations that allows the constructor to be called by having a mapping 
 * of parameter name -> parameter value
 * @author john
 * @param <T> - Type to return when the parser parses a node 
 */
public class AnnotationConstructor<T> {

	private static final Map<Class<?>,Object> PRIMITIVE_DEFAULTS = new HashMap<Class<?>,Object>(10);
	
	static {
		PRIMITIVE_DEFAULTS.put(boolean.class, Boolean.FALSE);
		PRIMITIVE_DEFAULTS.put(long.class, Long.valueOf(0));
		PRIMITIVE_DEFAULTS.put(int.class, Integer.valueOf(0));
		PRIMITIVE_DEFAULTS.put(short.class, Short.valueOf((short) 0));
		PRIMITIVE_DEFAULTS.put(byte.class, Byte.valueOf((byte) 0));
		PRIMITIVE_DEFAULTS.put(char.class, Character.valueOf('\0'));
	}
	
    private final Class<T> elementClass;

    /**
     * Constructor
     * @param elementClass
     * @param addToParentMethod
     * @param setParentMethod
     * @param setTextMethod
     * @param parseChildren
     * @throws ParserConfigurationException
     */
    public AnnotationConstructor(Class<T> elementClass) {
        this.elementClass = elementClass;
    }

    /**
     * Return constructors based on parameter annotations
     * @param parent
     * @param attribs
     * @return
     */
	protected Builder<T>[] getAnnotationConstructors(Object parent, Map<String,Object> parameters, String content) {
	    @SuppressWarnings("rawtypes")
    	Constructor[] cons = elementClass.getConstructors();
	    if (cons == null)
	    	return null;
		List<Builder<T>> list = new ArrayList<Builder<T>>(cons.length);
    	for (int i=0; i<cons.length; i++) {
    		@SuppressWarnings("unchecked")
    		Constructor<T> con = cons[i];
    		Builder<T> b = new Builder<T>(con, parent, parameters, content);
    		if (b.weight > 0)
    			list.add(b);
    	}
    	@SuppressWarnings("unchecked")
		Builder<T>[] result = list.toArray(new Builder[list.size()]);
		Arrays.sort(result);
    	return result;
    }
    
    /**
     * Build the object
     * @param parent
     * @param parameters
     * @param content
     * @return
     * @throws InvocationTargetException 
     * @throws IllegalArgumentException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     * @throws IntrospectionException 
     * @throws ParseException 
     */
    public T build(Object parent, Map<String,Object> parameters, String content) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException, ParseException {
        T result = null;
        if (content == null)
        	content = Utils.normalize(DataParser.format(parameters.get(Content.class.getName())));
        if (parent == null)
        	parent = parameters.get(Parent.class.getName());
        Builder<T>[] builders = getAnnotationConstructors(parent, parameters, content);
        if (builders == null || builders.length == 0)
        	throw new InstantiationException("No constructor with annotations");
        Exception err = null;
        if (builders != null) {
        	for (Builder<T> builder : builders) {
        		try {
        			result = builder.build();
        			if (result != null)
        				break;
                } catch (SecurityException e) {
                    // Ignore exception - try default constructor
                } catch (InstantiationException e) {
                	if (err == null)
                		err = e;
                } catch (IllegalAccessException e) {
                	if (err == null)
                		err = e;
                } catch (IllegalArgumentException e) {
                	if (err == null)
                		err = e;
                } catch (InvocationTargetException e) {
                	if (err == null)
                		err = e;
        		}
        	}
        }
        if (result != null) {
	        try {
	            link(parent, result);
	            return result;
	        } catch (IllegalArgumentException e) {
	        	if (err == null)
	        		err = e;
			}
        }
        
        if (err instanceof InstantiationException)
        	throw (InstantiationException) err;
        if (err instanceof IllegalAccessException)
        	throw (IllegalAccessException) err;
        if (err instanceof IllegalArgumentException)
        	throw (IllegalArgumentException) err;
    	throw new InstantiationException("Failed to instantiate object from type " + elementClass.getName());
    }
    
    /**
     * Link parent to child
     * @param parent
     * @param child
     * @return
     * @throws ParseException 
     */
    protected T link(Object parent, T child) throws ParseException {
    	if (parent == null || child == null) 
        	return child;

    	for (Method m : parent.getClass().getMethods()) {
    		Class<?>[] params = m.getParameterTypes();
    		Annotation a = m.getAnnotation(AddChild.class);
    		if (a != null) { 
    			switch (params.length) {
    			case 0 :	
        			throw new ParseException(m.toString() + " has annotation " + AddChild.class.getSimpleName() + ", but method has no parameters", 0);
    			case 1 :
    				if (params[0].isInstance(child)) {
						try {
							m.invoke(parent, child);
						} catch (IllegalAccessException e) {
							throw Utils.cast(e, ParseException.class);
						} catch (IllegalArgumentException e) {
							throw Utils.cast(e, ParseException.class);
						} catch (InvocationTargetException e) {
							throw Utils.cast(e, ParseException.class);
						}
    				}
    				break;
    			case 2 :
    				break;
    			default :	
        			throw new ParseException(m.toString() + " has annotation " + AddChild.class.getSimpleName() + ", but method has too many parameters", 0);
    			}
    		}
    	}
    	
    	for (Method m : child.getClass().getMethods()) {
    		Class<?>[] params = m.getParameterTypes();
    		Annotation a = m.getAnnotation(SetParent.class);
    		if (a != null) { 
    			switch (params.length) {
    			case 0 :	
        			throw new ParseException(m.toString() + " has annotation " + SetParent.class.getSimpleName() + ", but method has no parameters", 0);
    			case 1 :
    				if (params[0].isInstance(parent)) {
						try {
							m.invoke(child, parent);
						} catch (IllegalAccessException e) {
							throw Utils.cast(e, ParseException.class);
						} catch (IllegalArgumentException e) {
							throw Utils.cast(e, ParseException.class);
						} catch (InvocationTargetException e) {
							throw Utils.cast(e, ParseException.class);
						}
    				}
    				break;
    			default :	
        			throw new ParseException(m.toString() + " has annotation " + SetParent.class.getSimpleName() + ", but method has too many parameters", 0);
    			}
    		}
    	}
    	
    	return child;
    }

    /**
     * Build an object using annotations
     * @param elementClass
     * @param parent
     * @param parameters
     * @param content
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws IntrospectionException
     * @throws ParseException
     */
    public static <T> T build(Class<T> elementClass, Object parent, Map<String,Object> parameters, String content) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException, ParseException {
    	AnnotationConstructor<T> con = new AnnotationConstructor<T>(elementClass);
    	return con.build(parent, parameters, content);
    }

    /**
     * Build an object using annotations
     * @param elementClass
     * @param parameters
     * @param content
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws IntrospectionException
     * @throws ParseException
     */
    public static <T> T build(Class<T> elementClass, Map<String,Object> parameters, String content) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException, ParseException {
    	return build(elementClass, null, parameters, content);
    }

    /**
     * Build an object using annotations
     * @param elementClass
     * @param parameters
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws IntrospectionException
     * @throws ParseException
     */
    public static <T> T build(Class<T> elementClass, Map<String,Object> parameters) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException, ParseException {
    	return build(elementClass, null, parameters, null);
    }

    /**
     * Build an object using annotations
     * @param elementClass
     * @param parent
     * @param parameters
     * @return
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws IntrospectionException
     * @throws ParseException
     */
    public static <T> T build(Class<T> elementClass, Object parent, Map<String,Object> parameters) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IntrospectionException, ParseException {
    	return build(elementClass, parent, parameters, null);
    }
    
    protected static Object getPrimitiveDefault(Class<?> type) {
    	Object result = PRIMITIVE_DEFAULTS.get(type);
    	return (result != null ? result : DataParser.cast(Integer.valueOf(0), type));
    }
    
    /**
     * Class used to sort available annotation constructors
     * @author john
     *
     * @param <T>
     */
    protected static class Builder<T> implements Comparable<Builder<T>> {
    	private final Constructor<T> constructor;
    	private final Object[] parameters;
    	private final int weight;
    	public Builder(Constructor<T> constructor, Object parent, Map<String,Object> attribs, String content) {
    		this.constructor = constructor;
    		Class<?>[] types = constructor.getParameterTypes();
    		if (types != null && types.length > 0) {
	    		this.parameters = new Object[types.length];
	    		Object[] defaults = new Object[this.parameters.length];
	    		for (int i=0; i<types.length; i++) {
	    			if (types[i].isPrimitive()) {
	    				if (types[i] == boolean.class)
		    				defaults[i] = getPrimitiveDefault(types[i]);
	    			}
	    		}
    			Annotation[][] anno = constructor.getParameterAnnotations();
    			int w = 0;
    			for (int i=0; i<parameters.length; i++) {
    				for (Annotation a : anno[i]) {
    					if (Parent.class.isInstance(a)) {
    						this.parameters[i] = parent;
    						w += 10000;
    						break;
    					}
    					if (Content.class.isInstance(a)) {
    						this.parameters[i] = DataParser.cast(content, types[i]);
    						w += 5000;
    						break;
    					}
    					if (Param.class.isInstance(a)) {
    						String name = ((Param) a).value();
    						if (name == null) {
    							w -= 1000;
    						} else if (attribs.containsKey(name)) {
        						w += 100;
    							try {
            						this.parameters[i] = DataParser.cast(attribs.get(name), types[i]);
    							} catch (ClassCastException e) {
    								w -= 10;
    							}
    						} else {
        						w -= 1;
    						}
    					}
    					if (Default.class.isInstance(a)) {
    						String dflt = ((Default) a).value();
    						if (dflt != null) {
    							Object tmp = DataParser.cast(dflt, types[i]);
    							if (tmp != null)
    								defaults[i] = tmp;
    						}
    					}
    				}
    			}
    			for (int i=0; i<defaults.length; i++) {
    				if (defaults[i] != null && parameters[i] == null)
    					parameters[i] = defaults[i];
    			}
    			this.weight = w;
    		} else {
	    		this.parameters = new Object[0];
    			this.weight = 0;
    		}
    	}

    	public boolean hasAnnotations() {
    		return (weight > 0 ? true : false);
    	}
    	
    	/*
    	 * @see java.lang.Comparable#compareTo(java.lang.Object)
    	 */
    	@Override
    	public int compareTo(Builder<T> o) {
    		if (o == null)
    			return -1;
    		return Integer.valueOf(o.weight).compareTo(Integer.valueOf(weight));
    	}
    	
    	/**
    	 * Build the object
    	 * @return
    	 * @throws InstantiationException
    	 * @throws IllegalAccessException
    	 * @throws IllegalArgumentException
    	 * @throws InvocationTargetException
    	 */
    	public T build() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
    		return constructor.newInstance(parameters);
    	}
    }
}

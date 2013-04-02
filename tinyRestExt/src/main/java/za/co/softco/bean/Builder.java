/*******************************************************************************
 * Copyright (C) Bester Consulting 2010. All Rights reserved.
 * This file may be distributed under the Softco / L-Mobile Share License
 * 
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 31 Jan 2011
 *******************************************************************************/
package za.co.softco.bean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import za.co.softco.reflect.Content;
import za.co.softco.reflect.Default;
import za.co.softco.reflect.Param;
import za.co.softco.reflect.Parent;
import za.co.softco.text.BooleanParser;
import za.co.softco.text.CharacterParser;
import za.co.softco.text.DataParser;
import za.co.softco.text.DoubleParser;
import za.co.softco.text.FloatParser;
import za.co.softco.text.IntegerParser;
import za.co.softco.text.LongParser;
import za.co.softco.util.PropertyMap;

/**
 * A possible builder that allows a object to be constructed
 * @author john
 * @param <D>
 */
public class Builder<D> implements Comparable<Builder<D>> {
    private static final Map<Class<?>,Cast> CASTS = new HashMap<Class<?>, Cast>();

    private static interface Cast {
    	public Object cast(Object value);
    }
    
    private static final Cast INT_CAST = new Cast() {
    	public Object cast(Object value) {
    		return Integer.valueOf(IntegerParser.toInt(value));
    	}
    };
    
    private static final Cast LONG_CAST = new Cast() {
    	public Object cast(Object value) {
    		return Long.valueOf(LongParser.toLong(value));
    	}
    };
    

    private static final Cast SHORT_CAST = new Cast() {
    	public Object cast(Object value) {
    		return Short.valueOf((short) IntegerParser.toInt(value));
    	}
    };
    
    private static final Cast BYTE_CAST = new Cast() {
    	public Object cast(Object value) {
    		return Byte.valueOf((byte) IntegerParser.toInt(value));
    	}
    };
    
    private static final Cast FLOAT_CAST = new Cast() {
    	public Object cast(Object value) {
    		return Float.valueOf(FloatParser.toFloat(value));
    	}
    };
    
    private static final Cast DOUBLE_CAST = new Cast() {
    	public Object cast(Object value) {
    		return Double.valueOf(DoubleParser.toDouble(value));
    	}
    };
    
    private static final Cast BOOLEAN_CAST = new Cast() {
    	public Object cast(Object value) {
    		return Boolean.valueOf(BooleanParser.toBoolean(value));
    	}
    };
    
    private static final Cast CHAR_CAST = new Cast() {
    	public Object cast(Object value) {
    		return Character.valueOf(CharacterParser.toChar(value));
    	}
    };
    
    static {
        CASTS.put(int.class, INT_CAST);
        CASTS.put(long.class, LONG_CAST);
        CASTS.put(short.class, SHORT_CAST);
        CASTS.put(byte.class, BYTE_CAST);
        CASTS.put(float.class, FLOAT_CAST);
        CASTS.put(double.class, DOUBLE_CAST);
        CASTS.put(boolean.class, BOOLEAN_CAST);
        CASTS.put(char.class, CHAR_CAST);
    }
    
    
	private final Constructor<D> constructor;
	private final String[] parameterNames;
	private final Object[] parameters;
	private final int[] indexes;
	private final int[] parentIndexes;
	private final int[] contentIndexes;
	private final int argumentMatches;
	private final float paramCount;
	private final float weight;
	
	private Builder(Constructor<D> constructor, String[] parameterNames, Object[] parameters, int[] metaIndexes, int[] parentIndexes, int[] contentIndexes, int argumentMatches) {
		this.constructor = constructor;
		this.parameterNames = parameterNames;
		this.parameters = parameters;
		this.indexes = metaIndexes;
		this.parentIndexes = parentIndexes;
		this.contentIndexes = contentIndexes;
		this.argumentMatches = argumentMatches;
		this.paramCount = (parameters != null ? parameters.length : 0);
		this.weight = (paramCount > 0 ? argumentMatches / paramCount : 0);
	}
	
	private Builder(Constructor<D> constructor, String[] parameterNames, int[] parentIndexes, int[] contentIndexes, int argumentMatches) {
		this(constructor, parameterNames, new Object[constructor.getParameterTypes().length], null, parentIndexes, contentIndexes, argumentMatches);
	}
	
	private Builder(Constructor<D> constructor, String[] parameterNames, int[] indexes, int[] parentIndexes, int[] contentIndexes, int argumentMatches) {
		this(constructor, parameterNames, new Object[constructor.getParameterTypes().length], indexes, parentIndexes, contentIndexes, argumentMatches);
	}
	
	/**
	 * Build the bean object
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public D build() throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return (D) constructor.newInstance(parameters);
	}

	/**
	 * Build the bean object using specified parameters. If no parameters are specified, then the default parameters
	 * will be used. Also, the default parameters will be used for any parameters not in the key set of the parameters
	 * specified.
	 * @param parent
	 * @param parameters
	 * @param content
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public D build(Object parent, Map<String,Object> parameters, String content) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (parameters == null || parameters.size() == 0) 
			return build();

		Class<?>[] types = constructor.getParameterTypes();
		Object[] params = new Object[types.length];
		for (int i=0; i<Math.min(params.length,types.length); i++) {
			String name = parameterNames[i]; 
			if (name != null) {
				if (parameters.containsKey(name))
					params[i] = DataParser.cast(parameters.get(parameterNames[i]), types[i]);
				else
					params[i] = this.parameters[i];
			}
		}
		if (parentIndexes != null && parent != null) {
			for (int i : parentIndexes)
				params[i] = DataParser.cast(parent, constructor.getParameterTypes()[i]);
		}
		if (contentIndexes != null && content != null) {
			for (int i : contentIndexes)
				params[i] = DataParser.cast(content, constructor.getParameterTypes()[i]);
		}
		
		return (D) constructor.newInstance(params);
	}

	/**
	 * Build the bean object using specified parameters. If no parameters are specified, then the default parameters
	 * will be used. Also, the default parameters will be used for any parameters not in the key set of the parameters
	 * specified.
	 * @param parameters
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 */
	public D build(Map<String,Object> parameters) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		return build(null, parameters, null);
	}
	
	/**
	 * Build an object using a result set
	 * @param rs
	 * @return
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws SQLException
	 */
	public D build(ResultSet rs) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SQLException {
		if (rs == null) 
			throw new IllegalArgumentException("rs parameter is required");

		Class<?>[] types = constructor.getParameterTypes();
		if (indexes != null && indexes.length == types.length) {
			Object[] params = new Object[types.length];
			for (int i=0; i<params.length; i++) {
				int ndx = indexes[i]; 
				if (ndx >= 0)
					params[i] = rs.getObject(ndx+1);
			}
			return (D) constructor.newInstance(params);
		}
		Map<String,Object> props = new PropertyMap<Object>();
		ResultSetMetaData meta = rs.getMetaData(); 
		for (int i=1; i<=meta.getColumnCount(); i++) {
			String name = meta.getColumnName(i);
			Object val = rs.getObject(i);
			props.put(name, val);
		}
		return build(props);
	}
	
	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return 0;
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object ref) {
		if (ref instanceof Builder)
			return ((Builder<?>) ref).constructor.equals(constructor);
		return false;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return constructor.toString();
	}

	/*
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(Builder<D> ref) {
		if (ref.argumentMatches < argumentMatches)
			return -1;
		if (ref.argumentMatches > argumentMatches)
			return 1;
		if (ref.weight < weight)
			return -1;
		if (ref.weight > weight)
			return 1;
		if (ref.paramCount < paramCount)
			return -1;
		if (ref.paramCount > paramCount)
			return 1;
		return 0;
	}

	/**
	 * Get a list of possible builders based only on parameter names
	 * @param beanClass
	 * @param parameters
	 * @return
	 */
	private static <T> Builder<T>[] getBuilders(Class<T> beanClass, Collection<String> parameters, boolean withIndexes) {
		if (beanClass == null || parameters == null || parameters.size() == 0)
			return null;

		Map<String,Integer> params = new HashMap<String,Integer>(parameters.size()*3);
		int ndx = 0;
		for (String param : parameters) {
			if (param != null)
				params.put(param.trim().toLowerCase(), Integer.valueOf(ndx));
			ndx++;
		}
		
    	@SuppressWarnings("unchecked")
		Constructor<T>[] constructors = (Constructor<T>[]) beanClass.getConstructors();
    	List<Builder<T>> possibleResults = new LinkedList<Builder<T>>();
    	for (Constructor<T> con : constructors) {
    		int matches = 0;
    		Class<?>[] types = con.getParameterTypes();
    		String[] names = new String[types.length];
    		int[] parentIndexes = null;
    		int[] contentIndexes = null;
    		Annotation[][] annotations = con.getParameterAnnotations();
    		for (int i=0; i<types.length; i++) {
    			Annotation[] anno = annotations[i];
    			for (Annotation a : anno) {
    				if (a instanceof Param) {
    					String name = ((Param) a).value();
    					if (name != null) {
    						name = name.trim();
	    					names[i] = name;
	    					if (name != null && params.containsKey(name.toLowerCase()))  
	    						matches++;
    					}
    				} else if (a instanceof Parent) {
    					matches++;
    					parentIndexes = append(parentIndexes, i);
    				} else if (a instanceof Content) {
    					matches++;
    					contentIndexes = append(contentIndexes, i);
    				}
    			}
			}
    		if (matches > 0) {
    			if (withIndexes) {
    				int[] indexes = new int[names.length];
    				Arrays.fill(indexes, -1);
    				for (int i=0; i<names.length; i++) {
    					String nm = names[i];
    					if (nm == null)
    						continue;
    					Integer tmp = params.get(nm.toLowerCase());
    					if (tmp != null)
    						indexes[i] = tmp.intValue();
    				}
    				possibleResults.add(new Builder<T>(con, names, indexes, parentIndexes, contentIndexes, matches));
    			} else {
    				possibleResults.add(new Builder<T>(con, names, parentIndexes, contentIndexes, matches));
    			}
    		}
    	}
    	if (possibleResults.size() == 0)
    		return null;
    	
    	@SuppressWarnings("unchecked")
		Builder<T>[] result = possibleResults.toArray(new Builder[possibleResults.size()]); 
    	if (result.length > 1)
    		Arrays.sort(result);
    	return result;
	}

	public static <T> Builder<T>[] getBuilders(Class<T> beanClass, Collection<String> parameters) {
		return getBuilders(beanClass, parameters, false);
	}
	
	/**
	 * Get a list of possible builders based only on parameter names
	 * @param beanClass
	 * @param parameters
	 * @return
	 * @throws SQLException 
	 */
	public static <T> Builder<T>[] getBuilders(Class<T> beanClass, ResultSetMetaData metaData) throws SQLException {
		Collection<String> columns = new ArrayList<String>(metaData.getColumnCount());
		for (int i=1; i<=metaData.getColumnCount(); i++)
			columns.add(metaData.getColumnName(i));
		return getBuilders(beanClass, columns, true);
	}
	
	/**
	 * Get a list of possible builders based on parameter names and values
	 * @param beanClass
	 * @param parameters
	 * @return
	 */
	public static <T> Builder<T>[] getBuilders(Class<T> beanClass, Object parent, Map<String,Object> parameters, String content) {
		if (beanClass == null || parameters == null || parameters.size() == 0)
			return null;

		parameters = PropertyMap.getPropertyMap(parameters);
		
		@SuppressWarnings("unchecked")
		Constructor<T>[] constructors = (Constructor<T>[]) beanClass.getConstructors();
    	List<Builder<T>> possibleResults = new LinkedList<Builder<T>>();
    	for (Constructor<T> con : constructors) {
    		int matches = 0;
    		Class<?>[] types = con.getParameterTypes();
    		String[] names = new String[types.length];
    		Object[] params = new Object[types.length];
    		int[] parentIndexes = null;
    		int[] contentIndexes = null;
    		Annotation[][] annotations = con.getParameterAnnotations();
    		ParamLoop:
    		for (int i=0; i<params.length; i++) {
    			Annotation[] anno = annotations[i];
				String name = null;
				String dflt = null;
    			for (Annotation a : anno) {
    				if (a instanceof Param) {
    					String tmp = ((Param) a).value();
    					if (parameters.containsKey(tmp)) { 
    						matches++;
    						name = tmp;
    					}
    				} else if (a instanceof Default) {
    					dflt = ((Default) a).value();
    				} else if (a instanceof Parent) {
						matches++;
						params[i] = DataParser.cast(parent, types[i]);
    					parentIndexes = append(parentIndexes, i);
						continue ParamLoop;
    				} else if (a instanceof Content) {
						matches++;
						params[i] = DataParser.cast(content, types[i]);
						contentIndexes = append(contentIndexes, i);
						continue ParamLoop;
    				}
    			}
				Object tmp = dflt;
				names[i] = name;
    			if (name != null) 
    				tmp = parameters.get(name);
				try {
					params[i] = DataParser.cast(tmp, types[i]);
				} catch (ClassCastException e) {
					// Ignore exception
				}
			}
    		if (matches > 0)
    			possibleResults.add(new Builder<T>(con, names, params, null, parentIndexes, contentIndexes, matches));
    	}
    	if (possibleResults.size() == 0)
    		return null;
    	
    	@SuppressWarnings("unchecked")
		Builder<T>[] result = possibleResults.toArray(new Builder[possibleResults.size()]); 
    	if (result.length > 1)
    		Arrays.sort(result);
    	return result;
	}

	private static int[] append(int[] array, int value) {
		if (array == null)
			return new int[] { value };
		int[] result = new int[array.length+1];
		System.arraycopy(array, 0, result, 0, array.length);
		result[array.length] = value;
		return result;
	}
	
	/**
	 * Get a list of possible builders based on parameter names and values
	 * @param beanClass
	 * @param parameters
	 * @return
	 */
	public static <T> Builder<T>[] getBuilders(Class<T> beanClass, Map<String,Object> parameters) {
		return getBuilders(beanClass, null, parameters, null);
	}
	
	/**
	 * Get a list of possible builders based only on parameter names
	 * @param beanClass
	 * @param parameters
	 * @return
	 */
	public static <T> Builder<T>[] getBuilders(Class<T> beanClass, String[] parameters) {
		return getBuilders(beanClass, Arrays.asList(parameters));
	}
	
	/**
	 * Get the preferred builder based only on parameter names
	 * @param beanClass
	 * @param parameters
	 * @return
	 */
	public static <T> Builder<T> getPreferredBuilder(Class<T> beanClass, String[] parameters) {
		Builder<T>[] tmp = getBuilders(beanClass, parameters);
		return (tmp != null && tmp.length > 0 ? tmp[0] : null);
	}
	
	/**
	 * Get the preferred builder based only on parameter names
	 * @param beanClass
	 * @param parameters
	 * @return
	 */
	public static <T> Builder<T> getPreferredBuilder(Class<T> beanClass, Collection<String> parameters) {
		Builder<T>[] tmp = getBuilders(beanClass, parameters);
		return (tmp != null && tmp.length > 0 ? tmp[0] : null);
	}
	
	/**
	 * Get the preferred builder based only on parameter names
	 * @param beanClass
	 * @param parameters
	 * @return
	 * @throws SQLException 
	 */
	public static <T> Builder<T> getPreferredBuilder(Class<T> beanClass, ResultSetMetaData metaData) throws SQLException {
		Builder<T>[] tmp = getBuilders(beanClass, metaData);
		return (tmp != null && tmp.length > 0 ? tmp[0] : null);
	}
	
	/**
	 * Get the preferred builder based only on parameter names and values
	 * @param beanClass
	 * @param parameters
	 * @return
	 */
	public static <T> Builder<T> getPreferredBuilder(Class<T> beanClass, Map<String,Object> parameters) {
		Builder<T>[] tmp = getBuilders(beanClass, null, parameters, null);
		return (tmp != null && tmp.length > 0 ? tmp[0] : null);
	}

	/**
	 * Get the preferred builder based only on parameter names and values
	 * @param beanClass
	 * @param parameters
	 * @return
	 */
	public static <T> Builder<T> getPreferredBuilder(Class<T> beanClass, Object parent, Map<String,Object> parameters, String content) {
		Builder<T>[] tmp = getBuilders(beanClass, parent, parameters, content);
		return (tmp != null && tmp.length > 0 ? tmp[0] : null);
	}

	/**
	 * Cast an object to be used as a parameter
	 * @param object
	 * @param type
	 * @return
	 */
	public static Object cast(Object object, Class<?> type) {
		Cast c = CASTS.get(type);
		if (c != null)
			return c.cast(object);
		if (type == null || object == null)
			return object;
		return DataParser.cast(object, type);
	}
	

}
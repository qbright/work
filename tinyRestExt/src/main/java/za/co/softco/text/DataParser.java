/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: DataParser.java,v $
 *  Revision 1.7  2008/01/11 10:34:23  remjohn
 *  Improve functionality
 *
 *  Revision 1.6  2007/12/22 19:35:14  remjohn
 *  Improve DataParser framework
 *
 *  Revision 1.5  2007/10/11 11:44:05  remjohn
 *  Improve casting of arrays
 *
 *  Revision 1.4  2007/10/05 00:48:23  remjohn
 *  Refactor to avoid warnings
 *
 *  Revision 1.3  2007/09/07 09:29:34  remjohn
 *  Apply changes from bester package
 *
 *  Revision 1.2  2007/09/07 07:38:33  remjohn
 *  Use PropertyMap in stead of MapProxy
 *
 *  Revision 1.1  2007/08/15 13:05:58  rembrink
 *  Added to CVS
 *
 *  Revision 1.1  2007/08/05 08:31:11  john
 *  Converted base package to za.co.softco
 *
 *  Revision 1.1  2007/06/14 10:21:06  goofyxp
 *  Split besterBase from bester library
 *
 *  Revision 1.21  2006/12/28 15:34:01  goofyxp
 *  Use java.reflect.Array.newInstance() in stead of factory
 *
 *  Revision 1.20  2006/10/24 17:44:29  goofyxp
 *  Added parsers for ExtendedDataItem[] and DataItem[]
 *
 *  Revision 1.19  2006/10/10 09:46:17  goofyxp
 *  Imrpove casting to enumeration
 *
 *  Revision 1.18  2006/10/04 17:00:03  obelix
 *  Added parser for MapProxy
 *
 *  Revision 1.17  2006/09/29 11:18:49  obelix
 *  Added BigDecimal and BigInteger parsers
 *
 *  Revision 1.16  2006/09/13 10:15:38  obelix
 *  Improve functionality
 *
 *  Revision 1.15  2006/09/13 09:31:32  obelix
 *  Added PropertyParser
 *
 *  Revision 1.14  2006/09/13 09:04:47  obelix
 *  Added parsers for DataItem and ExtendedDataItem
 *
 *  Revision 1.13  2006/09/01 14:24:32  obelix
 *  Added int[] parser
 *
 *  Revision 1.12  2006/08/18 20:07:24  obelix
 *  Improve handling of arrays
 *
 *  Revision 1.11  2006/07/28 15:16:24  obelix
 *  Added parsing of enumerated classes
 *
 *  Revision 1.10  2006/07/04 11:27:35  obelix
 *  Added StringParser
 *
 *  Revision 1.9  2006/06/20 22:32:05  obelix
 *  Optimize
 *
 *  Revision 1.8  2006/06/16 14:57:48  obelix
 *  Optimize
 *
 *  Revision 1.7  2006/05/21 11:40:11  obelix
 *  Added ObjectParser
 *
 *  Revision 1.6  2006/05/12 14:08:06  obelix
 *  Improve parsing and casting of dates, times and timestamps
 *
 *  Revision 1.5  2006/04/13 08:59:29  goofyxp
 *  If a Date object does not contain a valid date, then format it as a time
 *
 *  Revision 1.4  2006/03/29 07:14:08  goofyxp
 *  Use generics functions to avoid type casting
 *
 *  Revision 1.3  2006/03/18 19:53:25  obelix
 *  Restructure code to use Map<String, Object> in stead of Properties
 *
 *  Revision 1.2  2006/03/01 15:31:07  goofyxp
 *  Added InetAddress parsing
 *
 *  Revision 1.1  2006/01/10 14:58:47  goofyxp
 *  Renamed za.co.softco.parser package to za.co.softco.text
 *
 *  Revision 1.2  2005/12/05 09:23:35  obelix
 *  Add comments
 *  Add format(Object, String)
 *
 *  Created on 01-Dec-2005
 *******************************************************************************/
package za.co.softco.text;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.IntrospectionException;
import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import org.w3c.dom.Document;

import za.co.softco.bean.BeanManager;
import za.co.softco.bean.DefaultBeanManager;
import za.co.softco.util.PropertyMap;
import za.co.softco.util.Utils;

/**
 * Class used to parse a string and return an object of a specific class
 * @author John Bester
 * @model
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public class DataParser {
    private static final int PUBLIC_STATIC = (Modifier.PUBLIC | Modifier.STATIC); 
    
    private static Map<Class<?>, Parser<?>> parsers = new HashMap<Class<?>, Parser<?>>();
    private static Map<Class<?>, Format> formats = new HashMap<Class<?>, Format>();
    private static Map<Class<?>, Boolean> collectionClasses = new HashMap<Class<?>, Boolean>();
    private static Map<Class<?>, Class<?>> primitiveClassMap = new HashMap<Class<?>, Class<?>>();

    private static int precisionDigits = 4;

    /**
     * Register all standard parsers
     */
    static {
        parsers.put(byte.class, new ByteParser());
        parsers.put(int.class, new IntegerParser());
        parsers.put(long.class, new LongParser());
        parsers.put(short.class, new ShortParser());
        parsers.put(float.class, new FloatParser());
        parsers.put(double.class, new DoubleParser());
        parsers.put(boolean.class, new BooleanParser());
        parsers.put(char.class, new CharacterParser());

        parsers.put(Object.class, new ObjectParser());
        parsers.put(String.class, new StringParser());
        parsers.put(Byte.class, new ByteParser());
        parsers.put(Integer.class, new IntegerParser());
        parsers.put(BigInteger.class, new BigIntegerParser());
        parsers.put(Long.class, new LongParser());
        parsers.put(Short.class, new ShortParser());
        parsers.put(Float.class, new FloatParser());
        parsers.put(Double.class, new DoubleParser());
        parsers.put(BigDecimal.class, new BigDecimalParser());
        parsers.put(Boolean.class, new BooleanParser());
        parsers.put(Character.class, new CharacterParser());
        parsers.put(File.class, new FileParser());

        parsers.put(Number.class, new LongParser());

        Parser<?> inetParser = new InetAddressParser();
        parsers.put(InetAddress.class, inetParser);
        parsers.put(Inet4Address.class, inetParser);
        parsers.put(Inet6Address.class, inetParser);

        parsers.put(byte[].class, new ByteArrayParser());
        parsers.put(int[].class, new IntArrayParser());
        parsers.put(long[].class, new LongArrayParser());
        parsers.put(float[].class, new FloatArrayParser());
        parsers.put(double[].class, new DoubleArrayParser());
        parsers.put(boolean[].class, new BooleanArrayParser());
        parsers.put(java.util.Date.class, new TimestampParser());
        parsers.put(java.sql.Date.class, new DateParser());
        parsers.put(java.sql.Time.class, new TimeParser());
        parsers.put(java.sql.Timestamp.class, new TimestampParser());
        parsers.put(Color.class, new ColorParser());

        parsers.put(Rectangle2D.Double.class, new RectangleParser());
        parsers.put(Rectangle2D.Float.class, new RectangleParser());
        parsers.put(Rectangle2D.class, new RectangleParser());
        parsers.put(Rectangle.class, new RectangleParser());

        parsers.put(Point2D.Double.class, new PointParser());
        parsers.put(Point2D.Float.class, new PointParser());
        parsers.put(Point2D.class, new PointParser());
        parsers.put(Point.class, new PointParser());

        parsers.put(Dimension2D.class, new DimensionParser());
        parsers.put(Dimension.class, new DimensionParser());

        parsers.put(URL.class, new URLParser());
        parsers.put(URI.class, new URIParser());
        parsers.put(Proxy.class, new ProxyParser());
        parsers.put(Document.class, new XMLParser());
        
        PropertyParser mapParser = new PropertyParser();
        parsers.put(Map.class, mapParser);
        parsers.put(PropertyMap.class, mapParser);
        parsers.put(HashMap.class, mapParser);
        parsers.put(LinkedHashMap.class, mapParser);

        parsers.put(Map[].class, new ArrayParser<Map>(Map.class));
        parsers.put(PropertyMap[].class, new ArrayParser<PropertyMap>(PropertyMap.class));

        setPrecision(precisionDigits);
        
        primitiveClassMap.put(boolean.class, Boolean.class);
        
        primitiveClassMap.put(byte.class, Byte.class);
        primitiveClassMap.put(char.class, Character.class);
        primitiveClassMap.put(double.class, Double.class);
        primitiveClassMap.put(float.class, Float.class);
        primitiveClassMap.put(int.class, Integer.class);
        primitiveClassMap.put(long.class, Long.class);
        primitiveClassMap.put(short.class, Short.class);        
    }

    public static final Object unwrapJavascriptObject(Object obj) {
        if (obj == null)
            return null;
        try {
            obj = DynamicScriptObjectParser.unwrapNativeJavaObject(obj);
            obj = DynamicScriptObjectParser.unwrapNativeJavaArray(obj);
            obj = DynamicScriptObjectParser.unwrapNativeArray(obj);
        } catch (Throwable e) {
            // Ignore exception
        }
        return obj;
    }
    
    /**
     * Return the wrapper class for a primitive class
     * @param clazz
     * @return
     */
    public static Class<?> getPrimitiveClassWrapper(Class<?> clazz) throws IllegalArgumentException {
        if (clazz == null)
            throw new IllegalArgumentException("Class must be spoecified");
        if (!clazz.isPrimitive())
            return clazz;
        Class<?> result = primitiveClassMap.get(clazz);
        if (result != null)
            return result;
        throw new IllegalArgumentException("No wrapper class defined for " + clazz.getName());
    }
    
    /**
     * Return a parser for the specified data class.
     * @param dataClass
     */
    public static <T> Parser<T> getParser(Class<T> dataClass) throws ParseException {
        if (dataClass == null)
            throw new ParseException("Cannot instatiate a GenericParser without a class", 0);

        Parser result = parsers.get(dataClass);
        if (result == null) {
            for (Map.Entry<Class<?>, Parser<?>> entry : parsers.entrySet()) {
                if (dataClass.isAssignableFrom(entry.getKey())) {
                    result = entry.getValue();
                    break;
                }
            }
        }

        if (result == null) {
            for (Map.Entry<Class<?>, Parser<?>> entry : parsers.entrySet()) {
                if (entry.getKey() == Object.class)
                    continue;
                if (isDerivedFrom(entry.getKey(), dataClass)) {
                    result = entry.getValue();
                    break;
                }
            }
        }

        if (result == null) {
            for (Map.Entry<Class<?>, Parser<?>> entry : parsers.entrySet()) {
                if (isImplementationOf(entry.getKey(), dataClass)) {
                    result = entry.getValue();
                    break;
                }
            }
        }

        if (result == null && dataClass.isArray()) {
            result = new ArrayParser(dataClass.getComponentType());
            parsers.put(dataClass, result);
        }

        if (result == null)
            try {
                result = new GenericParser(dataClass);
                parsers.put(dataClass, result);
            } catch (InstantiationException e) {
                return NumberArrayParser.createParser(dataClass);
            }
        return result;
    }

    /**
     * Returns true if a parser is defined for a specific data class
     * @param c
     * @return
     * @model
     */
    public static final <T> boolean canParse(Class<? extends T> c) {
        if (c == null)
            return false;
        try {
            Parser<?> p = getParser(c);
            return (p != null && p.getClass() != GenericParser.class);
        } catch (ParseException e) {
            return false;
        }
    }

    /**
     * Returns true if a parser is defined for a specific data object
     * @param o
     * @return
     * @model
     */
    public static final boolean canParse(Object o) {
        return canParse((o != null ? o.getClass() : null));
    }

    /**
     * Register a parser for a specific class of object
     * @param c
     * @param parser
     * @model
     */
    public static final <T> void register(Class<T> c, Parser<T> parser) {
        parsers.put(c, parser);
    }

    /**
     * Register a format for a specific class of object
     * @param c
     * @param parser
     * @model
     */
    public static final <T> void registerFormat(Class<T> c, Format format) {
        formats.put(c, format);
    }

    /**
     * Change an object to an object that is excepted as a parameter in a prepared SQL statement.
     */
    public static final Object toSQL(Object object) {
        if (object instanceof java.util.Date)
            return new java.sql.Date(((java.util.Date) object).getTime());
        if (object instanceof StringBuffer)
            return ((StringBuffer) object).toString();
        return object;
    }

    /**
     * Parse a string and return an object of a specific class
     */
    public static final <T> T parse(String value, Class<T> dataClass) throws ParseException {
        value = Utils.normalize(value);
        if (value == null)
            return null;

        if (dataClass.isEnum()) {
            try {
                return (T) Enum.valueOf((Class<Enum>) dataClass, value);
            } catch (IllegalArgumentException e) {
                try {
                    Method parse = dataClass.getMethod("parse", String.class);
                    if (parse != null && parse.getReturnType() == dataClass && (parse.getModifiers() & PUBLIC_STATIC) == PUBLIC_STATIC) {
                        try {
                            return (T) parse.invoke(null, value);
                        } catch (Exception e3) {
                            // Ignore exception
                        }
                    }
                } catch (Exception e2) {
                    // Ignore exception
                }
                try {
                    int ivalue = Integer.parseInt(value);
                    if (ivalue >= 0 && ivalue < dataClass.getEnumConstants().length)
                        return dataClass.getEnumConstants()[ivalue];
                } catch (NumberFormatException e2) {
                    //
                }
                try {
                    return (T) Enum.valueOf((Class<Enum>) dataClass, value.toUpperCase());
                } catch (IllegalArgumentException e2) {
                    Enum[] enums = ((Class<Enum>) dataClass).getEnumConstants();
                    for (Enum en : enums)
                        if (en.toString().replaceAll("\\_", "").equalsIgnoreCase(value))
                            return (T) en;
                    if (isNumber(value)) {
                        try {
                            int index = Integer.parseInt(value);
                            if (index >= 0 && index < enums.length)
                                return (T) enums[index];
                        } catch (NumberFormatException e3) {
                            throw new ClassCastException("Enumeration index out of bouds: " + dataClass.getName() + "(" + value + ")");
                        }
                    }
                    throw new ClassCastException("Could not find enumeration: " + dataClass.getName() + "(" + value + ")");
                }
            }
        }

        Parser<T> parser = getParser(dataClass);
        if (parser != null)
            return parser.parse(value);
        throw new ParseException("No parser defined for class " + dataClass, 0);
    }

    /**
     * Returns true if the class is a standard Java data class such as Integer or String 
     * or any of the primitive classes.
     * @param dataClass
     * @return
     */
    public static final boolean isSimpleDataClass(Class<?> dataClass) {
        if (dataClass.isPrimitive())
            return true;
        if (dataClass.equals(String.class))
            return true;
        if (dataClass.equals(Boolean.class))
            return true;
        while (dataClass != null) {
            if (dataClass == Number.class)
                return true;
            dataClass = dataClass.getSuperclass();
        }
        return false;
    }
    
    /**
     * Determine whether an object represents a collection or array
     * @param data
     * @return
     */
    public static boolean isCollection(Object data) {
        if (data == null)
            return false;
        if (data instanceof Collection)
            return true;
        if (data instanceof Class)
            return ((Class) data).isArray();
        if (data.getClass().isArray())
            return true;
        return false;
    }

    /**
     * Cast an object to an array of objects
     * @param data
     * @return
     */
    public static Object[] toArray(Object data) {
        if (data == null)
            return null;
        if (data instanceof Class)
            throw new IllegalArgumentException("Class not allowed as parameter");
        return new ArrayParser(Object.class).cast(data);
    }
    
    /**
     * Cast a value to a bean
     * @param <T>
     * @param value
     * @param dataClass
     * @return
     */
    public static <T> T beanCast(Object value, Class<T> dataClass) {
        try {
            T result = dataClass.newInstance();
            BeanManager mgr = new DefaultBeanManager(result);
            if (value instanceof Map)
                mgr.setProperties(PropertyMap.getPropertyMap((Map) value));
            else
                mgr.setProperties(new DefaultBeanManager(value).getProperties());
            return result;
        } catch (InstantiationException e) {
            throw new ClassCastException(e.getMessage());
        } catch (IllegalAccessException e) {
            throw new ClassCastException(e.getMessage());
        } catch (IntrospectionException e) {
            throw new ClassCastException(e.getMessage());
        }
    }

    /**
     * Cast a value to an array of beans
     * @param <T>
     * @param value
     * @param elementClass
     * @return
     */
    public static <T> T[] beanArrayCast(Object value, Class<T> elementClass) {
        if (value == null)
            return null;
        if (elementClass == null)
            throw new IllegalArgumentException("No element class specified");
        if (value instanceof Collection)
            value = toArray(value);
        else if (!value.getClass().isArray())
            value = new Object[] { value };
        int len = Array.getLength(value);
        T[] result = (T[]) Array.newInstance(elementClass, len);
        for (int i=0; i<len; i++)
            result[i] = beanCast(Array.get(value, i), elementClass);
        return result;
    }
    
    /**
     * Determines if a class is an enumeration
     * @param dataClass
     * @return
     */
    public static final boolean isEnumeration(Class<?> dataClass) {
        while (dataClass != null) {
            if (dataClass == Enum.class)
                return true;
            dataClass = dataClass.getSuperclass();
        }
        return false;
    }

    /**
     * Convert a value to a number. If the value is an integer, an object of type Integer is returned, else a Double is returned. If the input value
     * is null or not valid, then null is returned.
     * @param value - Any object that must be cast to a number
     * @throws NumberFormatException
     */
    public static Number parseNumber(Object value) throws NumberFormatException {
        if (value == null)
            return null;

        if (value instanceof Number) {
            if (((Number) value).intValue() == ((Number) value).doubleValue())
                return Integer.valueOf(((Number) value).intValue());
            return Double.valueOf(((Number) value).doubleValue());
        }
        try {
            String text = DoubleParser.normalizeDouble(value.toString());
            if (text == null)
                return null;
            double tmp = Double.parseDouble(text);
            if (tmp == (long) tmp) {
                long result = (long) tmp;
                if (result >= (Integer.MAX_VALUE / 2) || result <= (Integer.MIN_VALUE / 2))
                    return new Long(result);
                return Integer.valueOf((int) result);
            }
            return Double.valueOf(tmp);
        } catch (ParseException e) {
            throw new NumberFormatException(e.getMessage());
        }
    }

    /**
     * Parse a value to the best possible object. Possible results includes Boolean, Integer, Double or String
     * @param value - The string that must be parsed
     */
    public static Object parse(String value) {
        if (value == null)
            return null;

        value = value.trim();
        if (value.equalsIgnoreCase("true"))
            return Boolean.TRUE;
        if (value.equalsIgnoreCase("false"))
            return Boolean.FALSE;

        try {
            // Determine if there are any non-numerical characters in the string. If so, return the value as is
            if (!isNumber(value))
                return value;
            return parseNumber(value);
        } catch (NumberFormatException e) {
            return value;
        }
    }

    /**
     * Determine whether a class is a collection
     * @param clazz
     * @return
     */
    private static final boolean isCollection(Class<?> clazz) {
        if (clazz == null)
            return false;
        Boolean result = collectionClasses.get(clazz);
        if (result != null)
            return result.booleanValue();

        try {
            collectionClasses.put(clazz, Boolean.valueOf(clazz.newInstance() instanceof Collection));
        } catch (InstantiationException e) {
            // Ignore error
        } catch (IllegalAccessException e) {
            // Ignore error
        }

        for (Class<?> intfc : clazz.getInterfaces()) {
            if (intfc == Collection.class) {
                collectionClasses.put(clazz, Boolean.TRUE);
                return true;
            }
        }
        if (clazz == Collection.class) {
            collectionClasses.put(clazz, Boolean.TRUE);
            return true;
        }
        result = Boolean.valueOf(isCollection(clazz.getSuperclass()));
        collectionClasses.put(clazz, result);
        return result.booleanValue();
    }

    private static final Object multiItemPrecast(Object value, Class<?> dataClass) {
        if (value == null)
            return null;
        
        boolean fromMultiItem = (value.getClass().isArray() || value instanceof Collection);
        boolean toMultiItem = (dataClass.isArray() || isCollection(dataClass));

        if (value instanceof java.sql.Array) {
            try {
                value = ((java.sql.Array) value).getArray();
                if (value.getClass().isArray()) {
                    for (int i=0; i<Array.getLength(value); i++) {
                        Object tmp = Array.get(value, i);
                        if (tmp != null && tmp.equals("NULL"))
                            Array.set(value, i, null);
                    }
                }
                fromMultiItem = true;
            } catch (SQLException e) {
                throw new ClassCastException(e.getMessage());
            }
        }
                    
        if (fromMultiItem == toMultiItem)
            return value;

        if (fromMultiItem) {
            if (value instanceof Collection)
                value = ((Collection) value).toArray();
            if (value.getClass().isArray()) {
                for (int i = 0; i < Array.getLength(value); i++) {
                    Object result = Array.get(value, i);
                    if (result != null)
                        return result;
                }
                return null;
            }
        } else if (value instanceof String) {
            String tmp = ((String) value).trim();
            if (tmp.startsWith("[") && tmp.endsWith("]"))
                return value;
            return new Object[] { value };
        } else {
            return new Object[] { value };
        }
        return value;
    }

    /**
     * Determines whether once class is derived from another
     * @param superClass
     * @param subClass
     * @return
     */
    private static final boolean isDerivedFrom(Class<?> superClass, Class<?> subClass) {
        while (subClass != null) {
            if (subClass == superClass)
                return true;
            subClass = subClass.getSuperclass();
        }
        return false;
    }
    
    /**
     * Determines whether a specific class implements a specific interface
     * @param clazz
     * @param intfc
     * @return
     */
    private static boolean isImplementationOf(Class<?> clazz, Class<?> intfc) {
        if (!intfc.isInterface())
            return false;
        for (Class<?> impl : clazz.getInterfaces())
            if (impl == intfc)
                return true;
        return false;
    }
    
    /**
     * Convert an object to a specific type
     * @param value
     * @param dataClass
     * @return
     * @model
     */
    public static final <T> T cast(Object value, Class<T> dataClass) {
        if (value == null || dataClass == Object.class)
            return (T) value;
        if (dataClass.isInstance(value))
            return (T) value;
        value = unwrapJavascriptObject(value);
        if (isEnumeration(dataClass)) {
            Enum[] enums = ((Class<Enum>) dataClass).getEnumConstants();
            if (value instanceof Number) {
                int ndx = ((Number) value).intValue();
                if (ndx >= 0 && ndx < enums.length)
                    return (T) enums[ndx];
                throw new ClassCastException("Enumeration index out of bouds: " + dataClass.getName() + "(" + ndx + ")");
            }
            String val = Utils.normalize(value.toString());
            if (val == null)
                return null;
            try {
                return (T) Enum.valueOf((Class<Enum>) dataClass, val);
            } catch (IllegalArgumentException e) {
                try {
                    return (T) Enum.valueOf((Class<Enum>) dataClass, val.toUpperCase());
                } catch (IllegalArgumentException e2) {
                    for (Enum en : enums)
                        if (en.toString().replaceAll("\\_", "").equalsIgnoreCase(val))
                            return (T) en;
                    throw new ClassCastException("Could not find enumeration: " + dataClass.getName() + "(" + value + ")");
                }
            }
        }
        Parser<T> parser = null;
        try {
            parser = getParser(dataClass);
            if (parser != null) {
                if (parser.allowMultiItemPrecast())
                    value = multiItemPrecast(value, dataClass);
                return parser.cast(value);
            }
        } catch (ClassCastException e) {
            // Ignore error
        } catch (ParseException e) {
            // Ignore error
        }
        try {
            return dataClass.cast(value);
        } catch (ClassCastException e) {
            if (parser != null) {
                String text = Utils.normalize(format(value));
                if (text == null)
                    return null;
                
                try {
                    return parser.parse(text);
                } catch (ParseException e2) {
                    // Ignore exception - exception will be thrown in any case
                }
            }
            
            if (isSimpleDataClass(dataClass))
                throw e;
            
            if (!isCollection(dataClass))
                return beanCast(ArrayParser.firstSingleObject(value), dataClass);

            if (dataClass.isArray())
                return (T) beanArrayCast(value, dataClass.getComponentType());
            
            throw e;
        }
    }

    /**
     * Convert an object into a text string that can be parsed.
     * @param value - The value to format
     * @return The formatted value
     */
    public static final String format(Object value) {
        if (value == null)
            return null;

        if (value.getClass().isArray())
            return ArrayParser.formatArray(value);

        Format fmt = formats.get(value.getClass());
        if (fmt != null)
            return fmt.format(value);

        Parser<?> parser;
        try {
            parser = getParser(value.getClass());
            if (parser != null)
                return parser.toString(value);
        } catch (ParseException e) {
            // Ignore exception
        }
        return value.toString();
    }

    /**
     * Convert an object into a text string that can be parsed.
     * @param value - The value to format
     * @param format - The format to use
     * @return The value formatted as a string
     * @throws ParseException
     */
    public static final String format(Object value, String format) throws ParseException {
        if (format == null)
            throw new IllegalArgumentException("Format expected: Rather use format(Object)");

        if (value == null)
            return null;

        Parser<?> parser;
        parser = getParser(value.getClass());
        if (parser != null)
            return parser.format(value, format);
        return value.toString();
    }

    /**
     * Wrapper class for formatting a double
     * @param value
     * @return
     * @model
     */
    public static final String format(double value) {
        return format(new Double(value));
    }

    /**
     * Wrapper class for formatting a long
     * @param value
     * @return
     * @model
     */
    public static final String format(long value) {
        return format(new Long(value));
    }

    /**
     * Set the default precision for floating point numbers
     * @param digits
     * @model
     */
    public static void setPrecision(int digits) {
        precisionDigits = digits;
        NumberFormat fmt = NumberFormat.getInstance(Locale.US);
        fmt.setGroupingUsed(false);
        formats.put(Double.class, fmt);
        formats.put(Float.class, fmt);
        formats.put(BigDecimal.class, fmt);
        formats.put(Double.TYPE, fmt);
        formats.put(Float.TYPE, fmt);
        formats.put(Integer.class, fmt);
        formats.put(Long.class, fmt);
        formats.put(Short.class, fmt);
        formats.put(BigInteger.class, fmt);
        formats.put(Long.TYPE, fmt);
        formats.put(Integer.TYPE, fmt);
        formats.put(Short.TYPE, fmt);
    }

    /**
     * Determine if an object is a number (null and empty strings are not regarded as numbers)
     * @param value
     * @return
     */
    public static boolean isNumber(Object value) {
        if (value == null)
            return false;
        if (value instanceof Number)
            return true;
        if (value instanceof String) {
            String tmp = ((String) value).trim();
            if (tmp.length() == 0)
                return false;
            for (int i = 0; i < tmp.length(); i++) {
                if ("0123456789.,".indexOf(tmp.charAt(i)) < 0)
                    return false;
            }
            return true;
        }
        return false;
    }

}

/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 27 Jan 2011
 *******************************************************************************/
package za.co.softco.text.xml.parse;

import java.beans.IntrospectionException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import za.co.softco.bean.BeanManager;
import za.co.softco.bean.DefaultBeanManager;
import za.co.softco.text.model.NoParserException;
import za.co.softco.util.PropertyMap;
import za.co.softco.util.Utils;

/**
 * Default implementation of AttributeParserFactory
 * @author john
 */
public class DefaultAttributeParserFactory implements AttributeParserFactory {

    public static final String ISO_DATE_FORMAT = "yyyy-MM-dd";
    public static final String ISO_TIME_FORMAT = "hh:mm:ss";
    public static final String ISO_TIMESTAMP_FORMAT = ISO_DATE_FORMAT + " " + ISO_TIME_FORMAT;
    public static final String ISO_TIMESTAMP_FORMAT_TZ = ISO_TIMESTAMP_FORMAT + " zzz";
    
    protected static final int PUBLIC_STATIC = (Modifier.PUBLIC | Modifier.STATIC);
    private final Map<Class<?>,AttributeParser<?>> parsers = new HashMap<Class<?>,AttributeParser<?>>();

    /**
     * Constructor
     * @throws ParserConfigurationException
     */
    public DefaultAttributeParserFactory() throws ParserConfigurationException {
        register(String.class, new TextParser());
        register(Boolean.class, new BooleanParser());
        register(Double.class, new NumberParser<Double>(Double.class));
        register(Float.class, new NumberParser<Float>(Float.class));
        register(Long.class, new NumberParser<Long>(Long.class));
        register(Integer.class, new NumberParser<Integer>(Integer.class));
        register(Short.class, new NumberParser<Short>(Short.class));
        register(Byte.class, new NumberParser<Byte>(Byte.class));
        register(BigInteger.class, new NumberParser<BigInteger>(BigInteger.class));
        register(BigDecimal.class, new NumberParser<BigDecimal>(BigDecimal.class));
        register(Timestamp.class, new ISOTimestampParser());
        register(Date.class, new ISODateParser());
        register(Time.class, new ISOTimeParser());
        register(java.util.Date.class, new DefaultDateParser());
        registerPrimitive(long.class, Long.class);
        registerPrimitive(int.class, Integer.class);
        registerPrimitive(short.class, Short.class);
        registerPrimitive(byte.class, Byte.class);
        registerPrimitive(double.class, Double.class);
        registerPrimitive(float.class, Float.class);
        registerPrimitive(boolean.class, Boolean.class);
    }
    
    /*
     * @see za.co.softco.model.parser.AttributeParserFactory#getParser(java.lang.Class)
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> AttributeParser<T> getParser(Class<T> type) throws ParseException {
        AttributeParser<T> result = (AttributeParser<T>) parsers.get(type);
        if (result != null)
            return result;
        if (type.isEnum()) {
            result = new EnumParser<T>(type);
            parsers.put(type, result);
            return result;
        }
        for (Map.Entry<Class<?>,AttributeParser<?>> p : parsers.entrySet()) {
            if (type.isAssignableFrom(p.getKey())) {
                parsers.put(type, p.getValue());
                return (AttributeParser<T>) p.getValue();
            }
        }
        try {
            result = new ReflectionParser<T>(type);
            parsers.put(type, result);
            return result;
        } catch (ParserConfigurationException e) {
            throw new NoParserException(type);
        }
    }

    /*
     * 
     * @see za.co.softco.model.parser.AttributeParserFactory#parse(java.lang.String, java.lang.Class)
     */
    @Override
    public <T> T parse(String value, Class<T> type) throws ParseException {
        if (value == null)
            return null;
        value = value.trim();
        if (value.length() == 0)
            return null;
        AttributeParser<T> parser = getParser(type);
        if (parser == null)
            throw new ParseException("No parser found for type: " + type.getName(), 0);
        return parser.parse(value);
    }
    
    /*
     * @see za.co.softco.model.parser.AttributeParserFactory#setAttributes(java.lang.Object, java.util.Map)
     */
    @Override
    public void setAttributes(Object bean, Map<String, String> attribs) throws ParseException {
        try {
            BeanManager<Object> mgr = new DefaultBeanManager<Object>(bean);
            Map<String,Class<?>> types = mgr.getPropertyTypes();
            Map<String,Object> props = new PropertyMap<Object>(attribs.size());
            for (Map.Entry<String,String> attrib : attribs.entrySet()) {
                Class<?> type = types.get(attrib.getKey());
                if (type != null) {
                    try {
                        props.put(attrib.getKey(), parse(attrib.getValue(), type));
                    } catch (NoParserException e) {
                        System.err.println("No parser defined for type: " + bean.getClass().getName() + "#" + attrib.getKey() + " (" + attrib.getKey() + ")");
                    }
                } else {
                    System.err.println("Attribute type not found: " + bean.getClass().getName() + "#" + attrib.getKey() + " (" + attrib.getKey() + ")");
                }
            }
            
            mgr.setProperties(props);
        } catch (IntrospectionException e) {
            throw Utils.cast(e, ParseException.class);
        }
    }
    
    /**
     * Register a parser
     * @param <T>
     * @param type
     * @param parser
     */
    public <T> void register(Class<T> type, AttributeParser<T> parser) {
        parsers.put(type, parser);
    }
    
    private void registerPrimitive(Class<?> primitiveClass, Class<?> mirrorClass) {
        if (!primitiveClass.isPrimitive())
            throw new IllegalStateException(primitiveClass.getName() + " is not a primitive class");
        if (mirrorClass.isPrimitive())
            throw new IllegalStateException(mirrorClass.getName() + " is a primitive class");
        parsers.put(primitiveClass, parsers.get(mirrorClass));
    }
    
    /**
     * Get a valueOf
     * @param type
     * @param method
     * @param parameters
     * @return
     */
    protected static Method getPublicStaticMethod(Class<?> type, String method, Class<?>... parameters) {
        try {
            Method result = type.getMethod(method, parameters);
            if (!type.isAssignableFrom(result.getReturnType()))
                return null;
            if ((result.getModifiers() & PUBLIC_STATIC) == PUBLIC_STATIC)
                return result;
            return null;
        } catch (SecurityException e) {
            return null;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
    
    /**
     * Get a constructor to build an object from a string
     * @param <T>
     * @param type
     * @param parameters
     * @return
     */
    protected static <T> Constructor<T> getPublicStaticConstructor(Class<T> type, Class<?>... parameters) {
        try {
            Constructor<T> result = type.getConstructor(parameters);
            if ((result.getModifiers() & Modifier.PUBLIC) != 0)
                return result;
            return null;
        } catch (SecurityException e) {
            return null;
        } catch (NoSuchMethodException e) {
            return null;
        }
    }

    /**
     * Implementation for classes that have public static T valueOf(String) or
     * a constructor that takes only a string as an argument.
     * @author john
     * @param <T>
     */
    protected static class ReflectionParser<T> implements AttributeParser<T> {
        private final Class<T> type;
        private final Method valueOf;
        private final Constructor<T> constructor;

        public ReflectionParser(Class<T> type) throws ParserConfigurationException {
            this.type = type;
            valueOf = getPublicStaticMethod(type, "valueOf", String.class);
            constructor = getPublicStaticConstructor(type, String.class);
            if (valueOf == null && constructor == null)
                throw new ParserConfigurationException("No method or constructor found to parse " + type.getName());
        }
        
        @Override
        @SuppressWarnings("unchecked")
        public T parse(String value) throws ParseException {
            if (value == null)
                return null;
            if (valueOf != null) {
                try {
                    return (T) valueOf.invoke(null, value);
                } catch (IllegalArgumentException e) {
                    throw new ParseException("Could not parse " + value + " as " + type.getName(), 0);
                } catch (IllegalAccessException e) {
                    throw Utils.cast(e, IllegalStateException.class);
                } catch (InvocationTargetException e) {
                    throw new ParseException("Could not parse " + value + " as " + type.getName(), 0);
                }
            }
            if (constructor != null) {
                try {
                    return constructor.newInstance(value);
                } catch (IllegalArgumentException e) {
                    throw new ParseException("Could not parse " + value + " as " + type.getName(), 0);
                } catch (IllegalAccessException e) {
                    throw Utils.cast(e, IllegalStateException.class);
                } catch (InvocationTargetException e) {
                    throw new ParseException("Could not parse " + value + " as " + type.getName(), 0);
                } catch (InstantiationException e) {
                    throw Utils.cast(e, IllegalStateException.class);
                }
            }
            return null;
        }
        
        /*
         * @see za.co.softco.model.parser.AttributeParser#toString(java.lang.Object)
         */
        @Override
        public String toString(T value) {
            return (value != null ? value.toString() : null);
        }
    }

    /**
     * Implementation for all numeric types
     * @author john
     *
     * @param <T>
     */
    protected static class NumberParser<T extends Number> extends ReflectionParser<T> implements PrimitiveAttributeParser<T> {
        public NumberParser(Class<T> type) throws ParserConfigurationException {
            super(type);
        }

        /*
         * @see za.co.softco.model.parser.PrimitiveAttributeParser#parsePrimitive(java.lang.String)
         */
        @Override
        public T parsePrimitive(String value) throws ParseException {
            if (value == null || value.trim().length() == 0)
                value = "0";
            return parse(value);
        }
    }
    
    /**
     * Implementation for Boolean values
     * @author john
     */
    protected static class BooleanParser implements PrimitiveAttributeParser<Boolean> {

        @Override
        public Boolean parse(String value) throws ParseException {
            if (value == null)
                return null;
            return Boolean.valueOf(za.co.softco.text.BooleanParser.toBoolean(value));
        }
        
        /*
         * @see za.co.softco.model.parser.PrimitiveAttributeParser#parsePrimitive(java.lang.String)
         */
        @Override
        public Boolean parsePrimitive(String value) throws ParseException {
            if (value == null || value.trim().length() == 0)
                return Boolean.FALSE;
            Boolean result = parse(value);
            return (result != null ? result : Boolean.FALSE);
        }
        
        /*
         * @see za.co.softco.model.parser.AttributeParser#toString(java.lang.Object)
         */
        @Override
        public String toString(Boolean value) {
            return (value != null ? value.toString() : null);
        }
    }

    /**
     * Implementation for String values
     * @author john
     */
    protected static class TextParser implements AttributeParser<String> {

        @Override
        public String parse(String value) throws ParseException {
            return value;
        }
        
        /*
         * @see za.co.softco.model.parser.AttributeParser#toString(java.lang.Object)
         */
        @Override
        public String toString(String value) {
            return value;
        }
    }

    /**
     * Class allowing various date formats (the first is used when converting a date to text)
     * @author john
     */
    protected static class DateParser implements AttributeParser<java.util.Date> {
        private final SimpleDateFormat[] allowedFormats;
        public DateParser(String... allowedFormats) {
            this.allowedFormats = new SimpleDateFormat[allowedFormats.length];
            for (int i=0; i<allowedFormats.length; i++) {
                this.allowedFormats[i] = new SimpleDateFormat(allowedFormats[i]);
                this.allowedFormats[i].setLenient(false);
            }
        }
        @Override
        public java.util.Date parse(String value) throws ParseException {
            if (value == null)
                return null;
            value = value.trim();
            if (value.length() == 0)
                return null;
            
            ParseException err = null;
            for (SimpleDateFormat fmt : allowedFormats) {
                try {
                    return fmt.parse(value);
                } catch (ParseException e) {
                    if (err == null)
                        err = e;
                    err = e;
                }
            }
            if (err != null)
                throw err;
            throw new ParseException("Could not parse " + value + " as date", 0);
        }
        
        @Override
        public String toString(java.util.Date value) {
            if (value != null)
                return allowedFormats[0].format(value);
            return null;
        }
    }

    /**
     * Used for parsing XML timestamp values
     * @author john
     */
    protected static class ISOTimestampParser implements AttributeParser<Timestamp> {
        private final DateParser parser;
        public ISOTimestampParser() {
            this.parser = new DateParser(ISO_TIMESTAMP_FORMAT, ISO_TIMESTAMP_FORMAT_TZ);
        }
        @Override
        public Timestamp parse(String value) throws ParseException {
            java.util.Date result = parser.parse(value);
            return (result != null ? new Timestamp(result.getTime()) : null);
        }
        @Override
        public String toString(Timestamp value) {
            return parser.toString(value);
        }
    }

    /**
     * Used for parsing XML date values
     * @author john
     */
    protected static class ISODateParser implements AttributeParser<Date> {
        private final DateParser parser;
        public ISODateParser() {
            this.parser = new DateParser(ISO_DATE_FORMAT);
        }
        @Override
        public Date parse(String value) throws ParseException {
            java.util.Date result = parser.parse(value);
            return (result != null ? new Date(result.getTime()) : null);
        }
        @Override
        public String toString(Date value) {
            return parser.toString(value);
        }
    }
    
    /**
     * Used for parsing XML time values
     * @author john
     */
    protected static class ISOTimeParser implements AttributeParser<Time> {
        private final DateParser parser;
        public ISOTimeParser() {
            this.parser = new DateParser(ISO_TIME_FORMAT);
        }
        @Override
        public Time parse(String value) throws ParseException {
            java.util.Date result = parser.parse(value);
            return (result != null ? new Time(result.getTime()) : null);
        }
        @Override
        public String toString(Time value) {
            return parser.toString(value);
        }
    }

    /**
     * Used for default XML date parsing
     * @author john
     */
    protected static class DefaultDateParser extends DateParser {
        public DefaultDateParser() {
            super(ISO_TIMESTAMP_FORMAT, ISO_TIMESTAMP_FORMAT_TZ, ISO_DATE_FORMAT, ISO_TIME_FORMAT);
        }
    }

    /**
     * Parser for enumerated types
     * @author john
     * @param <T>
     */
    protected static class EnumParser<T> implements AttributeParser<T> {

        private static final int PUBLIC_STATIC = (Modifier.PUBLIC | Modifier.STATIC); 
        private final Class<T> type;
        
        public EnumParser(Class<T> type) {
            if (!type.isEnum())
                throw new IllegalArgumentException(type.getName() + " is not an enumeration");
            this.type = type;
        }
        
        @SuppressWarnings("unchecked")
        @Override
        public T parse(String value) throws ParseException {
            if (value == null)
                return null;
            value = value.trim();
            if (value.length() == 0)
                return null;
            for (T en : type.getEnumConstants())
                if (en.toString().equalsIgnoreCase(value))
                    return en;
            try {
                Method parse = type.getMethod("parse", String.class);
                if (parse != null && parse.getReturnType() == type && (parse.getModifiers() & PUBLIC_STATIC) == PUBLIC_STATIC) {
                    try {
                        return (T) parse.invoke(null, value);
                    } catch (Exception e) {
                        // Ignore exception
                    }
                }
            } catch (Exception e) {
                // Ignore exception
            }
            try {
                int ivalue = Integer.parseInt(value);
                if (ivalue >= 0 && ivalue < type.getEnumConstants().length)
                    return type.getEnumConstants()[ivalue];
            } catch (Exception e) {
                // Ignore exception
            }
            throw new ParseException("Could not parse " + value + " as " + type.getName(), 0);
        }

        @Override
        public String toString(T value) {
            return (value != null ? value.toString() : null);
        }
        
    }

}

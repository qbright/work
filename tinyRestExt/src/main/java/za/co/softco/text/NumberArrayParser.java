/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: NumberArrayParser.java,v $
 *  Revision 1.3  2007/12/22 19:35:14  remjohn
 *  Improve DataParser framework
 *
 *  Revision 1.2  2007/10/05 00:48:23  remjohn
 *  Refactor to avoid warnings
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
 *  Revision 1.6  2006/12/28 09:15:43  obelix
 *  Add <?> to avoid generics warnings
 *
 *  Revision 1.5  2006/11/28 12:22:04  obelix
 *  Fix potential null pointer bug
 *
 *  Revision 1.4  2006/03/18 19:53:25  obelix
 *  Restructure code to use Map<String, Object> in stead of Properties
 *
 *  Revision 1.3  2006/03/10 07:44:08  goofyxp
 *  Added generics
 *
 *  Revision 1.2  2006/03/01 23:51:40  goofyxp
 *  Convert to Java 5 syntax
 *
 *  Revision 1.1  2006/01/10 14:58:47  goofyxp
 *  Renamed za.co.softco.parser package to za.co.softco.text
 *
 *  Revision 1.2  2005/12/05 09:24:48  obelix
 *  Add comments
 *  Implement new format(Object, String) function in Parser interface to throw a default exception
 *
 *  Created on 01-Dec-2005
 *******************************************************************************/
package za.co.softco.text;

import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.util.LinkedHashMap;
import java.util.Map;

import za.co.softco.util.Utils;

/**
 * Abstract class that parse / format objects that can be represented by a sequence of numeric values
 * @author john
 * @model
 */
public abstract class NumberArrayParser<T> implements Parser<T> {
    @SuppressWarnings("unchecked")
    private static final Class<? extends Number>[] NUMBER_CLASSES = new Class[] { 
        Integer.TYPE, 
        Long.TYPE, 
        Double.TYPE, 
        Float.TYPE, 
        Short.TYPE, 
        Byte.TYPE, 
        Integer.class, 
        Long.class, 
        Double.class, 
        Float.class, 
        Short.class,
        Byte.class 
    };

    /*
     * @see za.co.softco.parser.Parser#parse(java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public T parse(String value) throws ParseException {
        if (Utils.normalize(value) == null)
            return null;

        Class<? extends T> resultClass = null;
        String[] tmp = Utils.normalize(value.split("\\["));
        String dim = null;
        if (tmp.length > 1) {
            try {
                if (tmp[0].startsWith("Point2D") || tmp[0].startsWith("Rectangle2D"))
                    tmp[0] = "java.awt.geom." + tmp[0].replace(".", "$");
                resultClass = (Class<? extends T>) Class.forName(tmp[0]);
            } catch (ClassNotFoundException e) {
                System.err.println("Invalid dimension class: " + e.getMessage());
            }
            dim = tmp[1];
        } else if (tmp.length > 0) {
            dim = tmp[0];
        } else {
            dim = value.trim();
        }

        tmp = dim.split("\\]");
        if (tmp.length > 0)
            dim = tmp[0];

        String[] params = dim.split(",");

        if (dim.indexOf("=") >= 0) {
            Map<String, String> values = new LinkedHashMap<String, String>();
            for (String val : params) {
                String[] entry = val.split("=");
                if (entry.length == 2) {
                    String name = entry[0].toLowerCase().trim();
                    if (name.equals("width"))
                        name = "w";
                    else if (name.equals("height"))
                        name = "h";
                    values.put(name, entry[1]);
                } else {
                    throw new ParseException("Invalid format", 0);
                }
            }
            params = new String[values.size()];
            int i = 0;
            switch (params.length) {
            case 4:
                params[i++] = values.get("x");
                params[i++] = values.get("y");
                params[i++] = values.get("w");
                params[i++] = values.get("h");
                break;
            case 2:
                params[i++] = (values.containsKey("x") ? values.get("x") : values.get("w"));
                params[i++] = (values.containsKey("y") ? values.get("y") : values.get("h"));
                break;
            }
        }

        Class<? extends Number> numberClass = null;
        if (resultClass == null)
            numberClass = getPreferredNumberClass(params);
        return parse(resultClass, numberClass, params);
    }

    /**
     * Return a Number class that best represents the value
     * @param value
     * @return
     * @model
     */
    protected static Class<? extends Number> getPreferredNumberClass(String value) {
        if (value == null || value.length() == 0)
            return Integer.class;

        if ((value.indexOf('.') >= 0)) {
            if (value.trim().length() > 10)
                return Double.class;
            return Float.class;
        }
        if (value.trim().length() > 6)
            return Long.class;
        return Integer.class;
    }

    /**
     * Get the preferred Number class to best represent any of the values in an array of values
     * @param values
     * @return
     * @model
     */
    protected static Class<? extends Number> getPreferredNumberClass(String[] values) {
        if (values == null || values.length == 0)
            return Integer.class;

        boolean isFloat = false;
        int maxLen = 0;
        for (String val : values) {
            isFloat |= (val != null ? val.indexOf('.') >= 0 : false);
            if (val != null)
                maxLen = Math.max(maxLen, val.trim().length());
        }
        if (isFloat) {
            if (maxLen > 10)
                return Double.class;
            return Float.class;
        }
        if (maxLen > 6)
            return Long.class;
        return Integer.class;
    }

    /**
     * Get the preferred Number classes to best represent each of the values in an array of values
     * @param values
     * @return
     * @model
     */
    @SuppressWarnings("unchecked")
    protected static Class<? extends Number>[] getPreferredNumberClasses(String[] values) {
        if (values == null || values.length == 0)
            return new Class[0];

        Class<? extends Number>[] result = new Class[values.length];
        for (int i = 0; i < result.length; i++)
            result[i] = getPreferredNumberClass(values[i]);
        return result;
    }

    /**
     * Return the preferred constructor to create the resulting object with
     * @param resultClass
     * @param numberClass
     * @param count
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @model
     */
    @SuppressWarnings("unchecked")
    protected static <X> Constructor<X> getPreferredConstructor(Class<X> resultClass, Class<? extends Number> numberClass, int count) throws SecurityException, NoSuchMethodException {
        Class<? extends Number>[] params = new Class[count];
        for (int i = 0; i < count; i++)
            params[i] = numberClass;
        return resultClass.getConstructor(params);
    }

    /**
     * Return the preferred constructor to create the resulting object with
     * @param resultClass
     * @param numberClass
     * @param count
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @model
     */
    protected static <X> Constructor<X> getPreferredConstructor(Class<X> resultClass, int count) throws NoSuchMethodException {
        for (Class<? extends Number> numClass : NUMBER_CLASSES) {
            try {
                return getPreferredConstructor(resultClass, numClass, count);
            } catch (SecurityException e) {
                // Ignore exception
            } catch (NoSuchMethodException e) {
                // Ignore exception
            }
        }
        throw new NoSuchMethodException("Could not find a constructor for " + resultClass + " with " + count + " numeric parameters");
    }

    /**
     * Get the preferred class to represent the string to be parsed
     * @param validClasses
     * @param numberClass
     * @param count
     * @return
     * @throws NoSuchMethodException
     * @model
     */
    protected static Class<?> getPreferredClass(Class<? extends Number>[] validClasses, Class<? extends Number> numberClass, int count) throws NoSuchMethodException {
        for (Class<? extends Number> result : validClasses) {
            try {
                getPreferredConstructor(result, numberClass, count);
                return result;
            } catch (SecurityException e) {
                // Ignore exception
            } catch (NoSuchMethodException e) {
                // Ignore exception
            }
        }
        throw new NoSuchMethodException("Could not find a constructor with " + count + " numeric parameters");
    }

    /**
     * Get the preferred constructor for a specific class
     * @param validClasses
     * @param count
     * @return
     * @throws NoSuchMethodException
     * @model
     */
    protected static Class<? extends Number> getPreferredConstructor(Class<? extends Number>[] validClasses, int count) throws NoSuchMethodException {
        for (Class<? extends Number> result : validClasses) {
            try {
                getPreferredConstructor(result, count);
                return result;
            } catch (SecurityException e) {
                // Ignore exception
            } catch (NoSuchMethodException e) {
                // Ignore exception
            }
        }
        throw new NoSuchMethodException("Could not find a constructor with " + count + " numeric parameters");
    }

    /**
     * Get the constructor to use to create an object of a specific class
     * @param resultClass
     * @param paramClasses
     * @return
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @model
     */
    @SuppressWarnings("unchecked")
    public static <X> Constructor<X> getContructor(Class<X> resultClass, Class<?>[] paramClasses) throws SecurityException, NoSuchMethodException {
        try {
            return resultClass.getConstructor(paramClasses);
        } catch (SecurityException e) {
            throw e;
        } catch (NoSuchMethodException e) {
            for (Constructor<?> con : resultClass.getConstructors()) {
                Class<?>[] types = con.getParameterTypes();
                if (types.length != paramClasses.length)
                    continue;
                boolean isValid = true;
                for (int i = 0; i < types.length; i++) {
                    if (types[i] != paramClasses[i]) {
                        if ((types[i] != Number.class && types[i].getGenericSuperclass() != Number.class)
                                || (paramClasses[i] != Number.class && paramClasses[i].getGenericSuperclass() != Number.class))
                            isValid = false;
                    }
                }
                if (isValid)
                    return (Constructor<X>) con;
            }
        }
        throw new NoSuchMethodException();
    }

    /**
     * Abstract function that needs to be implemented to parse a specific class of object
     * @param resultClass
     * @param numberClass
     * @param values
     * @return
     * @throws ParseException
     * @model
     */
    protected abstract T parse(Class<? extends T> resultClass, Class<? extends Number> numberClass, String[] values) throws ParseException;

    /**
     * Create a parser for a specific object class
     * @param dataClass
     * @return
     * @throws ParseException
     * @model
     */
    @SuppressWarnings("unchecked")
    public static <X> Parser<X> createParser(Class<? extends X> dataClass) throws ParseException {
        if (dataClass == null)
            throw new ParseException("Cannot instatiate a NumberArrayParser without a class", 0);

        try {
            X tmp = dataClass.newInstance();
            if (tmp instanceof Rectangle2D)
                return (Parser<X>) new RectangleParser();
            if (tmp instanceof Point2D)
                return (Parser<X>) new PointParser();
            if (tmp instanceof Dimension2D)
                return (Parser<X>) new DimensionParser();
        } catch (InstantiationException e) {
            throw new ParseException(e.getMessage(), 0);
        } catch (IllegalAccessException e) {
            throw new ParseException(e.getMessage(), 0);
        }
        throw new ParseException("Unknown class (" + dataClass + ")", 0);
    }

    /*
     * @see za.co.softco.parser.Parser#toString(java.lang.Object)
     */
    @Override
    public String toString(Object value) {
        if (value == null)
            return null;

        String result = value.toString().toLowerCase();
        int pos = result.indexOf("[");
        if (pos >= 0)
            result = result.substring(pos);

        result = Utils.replaceString(result, "width", "w");
        return Utils.replaceString(result, "height", "h");
    }

    /*
     * @see za.co.softco.parser.Parser#format(java.lang.Object, java.lang.String)
     */
    @Override
    public String format(Object value, String format) {
        throw new IllegalStateException("Not yet implemented");
    }
}

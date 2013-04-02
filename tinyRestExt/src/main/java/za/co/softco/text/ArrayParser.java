/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: ArrayParser.java,v $
 *  Revision 1.4  2007/12/23 17:02:17  remjohn
 *  Fixed comments
 *
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
 *  Revision 1.3  2006/12/28 15:33:52  goofyxp
 *  Use java.reflect.Array.newInstance() in stead of factory
 *
 *  Revision 1.2  2006/09/01 14:24:19  obelix
 *  Fix array bug
 *
 *  Revision 1.1  2006/08/18 20:07:09  obelix
 *  Created
 *
 *******************************************************************************/
package za.co.softco.text;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * Parser to handle object arrays
 * @author John Bester
 * @model
 */
public class ArrayParser<T extends Object> implements Parser<T[]> {
    private final Class<T> elementClass;

    /**
     * Construct an ArrayParser object without specifying an element class
     * @param elementClass
     */
    public ArrayParser(Class<T> elementClass) {
        this.elementClass = elementClass;
    }

    /*
     * @see za.co.softco.text.Parser#allowMultiItemPrecast()
     */
    @Override
    public boolean allowMultiItemPrecast() {
        return true;
    }

    /*
     * @see za.co.softco.io.DataParser#parseValue(java.lang.String)
     */
    @Override
    @SuppressWarnings("unchecked")
    public T[] parse(String value) throws ParseException {
        if (value == null)
            return null;
        if (value.startsWith("[") && value.endsWith("]"))
            value = value.substring(1, value.length() - 1);
        else
            throw new ParseException("Array string must start be enclosed in []", 0);

        String[] values = value.split(",");
        T[] result = (T[]) Array.newInstance(elementClass, values.length);
        for (int i = 0; i < values.length; i++)
            result[i] = DataParser.parse(values[i], elementClass);
        return result;
    }

    /**
     * If the value is an array or a Collection, return the first object in the array/collection. If in turn the first object is an array or
     * Collection, recucursively call firstSingleObject
     * @param value
     * @return
     */
    public final T getFistElement(Object value) {
        return firstSingleObject(value, elementClass);
    }

    /**
     * If the value is an array or a Collection, return the first object in the array/collection. If in turn the first object is an array or
     * Collection, recucursively call firstSingleObject
     * @param value
     * @param elementClass
     * @return
     */
    public static final <D> D firstSingleObject(Object value, Class<D> elementClass) {
        return DataParser.cast(firstSingleObject(value), elementClass);
    }

    /**
     * If the value is an array or a Collection, return the first object in the array/collection. If in turn the first object is an array or
     * Collection, recursively call firstSingleObject
     * @param value
     * @return
     */
    public static final Object firstSingleObject(Object value) {
        if (value == null)
            return null;

        if (value instanceof Map<?,?>)
            value = ((Map<?, ?>) value).entrySet();

        if (value instanceof Collection<?>)
            value = ((Collection<?>) value).toArray();

        if (!value.getClass().isArray())
            return value;

        if (!value.getClass().getComponentType().isPrimitive()) {
            Object[] temp = (Object[]) value;
            if (temp.length > 0)
                return firstSingleObject(temp[0]);
            return null;
        }
        if (value instanceof int[])
            return (((int[]) value).length > 0 ? new Integer(((int[]) value)[0]) : null);
        if (value instanceof long[])
            return (((long[]) value).length > 0 ? new Long(((long[]) value)[0]) : null);
        if (value instanceof short[])
            return (((short[]) value).length > 0 ? new Short(((short[]) value)[0]) : null);
        if (value instanceof byte[])
            return (((byte[]) value).length > 0 ? new Byte(((byte[]) value)[0]) : null);
        if (value instanceof float[])
            return (((float[]) value).length > 0 ? new Float(((float[]) value)[0]) : null);
        if (value instanceof double[])
            return (((double[]) value).length > 0 ? new Double(((double[]) value)[0]) : null);
        if (value instanceof char[])
            return (((char[]) value).length > 0 ? new Character(((char[]) value)[0]) : null);
        if (value instanceof boolean[])
            return (((boolean[]) value).length > 0 ? new Boolean(((boolean[]) value)[0]) : null);
        throw new IllegalArgumentException("Cannot get the first element of an array type " + value.getClass().getName());
    }

    /**
     * Convert an array or
     * @param value
     * @return
     */
    public static final String formatArray(Object value) {
        if (value == null)
            return null;

        if (value instanceof Map<?,?>)
            value = ((Map<?, ?>) value).entrySet();

        if (value instanceof Collection<?>)
            value = ((Collection<?>) value).toArray();

        value = DataParser.unwrapJavascriptObject(value);
        
        if (!value.getClass().isArray())
            return DataParser.format(value);

        if (!value.getClass().getComponentType().isPrimitive())
            return Arrays.deepToString((Object[]) value);
        if (value instanceof int[])
            return Arrays.toString((int[]) value);
        if (value instanceof long[])
            return Arrays.toString((long[]) value);
        if (value instanceof short[])
            return Arrays.toString((short[]) value);
        if (value instanceof byte[])
            return Arrays.toString((byte[]) value);
        if (value instanceof float[])
            return Arrays.toString((float[]) value);
        if (value instanceof double[])
            return Arrays.toString((double[]) value);
        if (value instanceof char[])
            return Arrays.toString((char[]) value);
        if (value instanceof boolean[])
            return Arrays.toString((boolean[]) value);
        throw new IllegalArgumentException("Cannot format an array type " + value.getClass().getName());
    }

    /*
     * @see za.co.softco.parser.Parser#toString(java.lang.Object)
     */
    @Override
    public String toString(Object value) {
        return formatArray(value);
    }

    /*
     * @see za.co.softco.parser.Parser#format(java.lang.Object, java.lang.String)
     */
    @Override
    public String format(Object value, String format) {
        throw new IllegalStateException("Not yet implemented");
    }

    /**
     * Cast an array of primitives
     * @param data
     * @return
     */
    @SuppressWarnings("unchecked")
    private T[] castArray(int[] data) {
        T[] result = (T[]) Array.newInstance(elementClass, data.length);
        for (int i = 0; i < data.length; i++)
            result[i] = DataParser.cast(new Integer(data[i]), elementClass);
        return result;
    }

    /**
     * Cast an array of primitives
     * @param data
     * @return
     */
    @SuppressWarnings("unchecked")
    private T[] castArray(long[] data) {
        T[] result = (T[]) Array.newInstance(elementClass, data.length);
        for (int i = 0; i < data.length; i++)
            result[i] = DataParser.cast(new Long(data[i]), elementClass);
        return result;
    }

    /**
     * Cast an array of primitives
     * @param data
     * @return
     */
    @SuppressWarnings("unchecked")
    private T[] castArray(short[] data) {
        T[] result = (T[]) Array.newInstance(elementClass, data.length);
        for (int i = 0; i < data.length; i++)
            result[i] = DataParser.cast(new Short(data[i]), elementClass);
        return result;
    }

    /**
     * Cast an array of primitives
     * @param data
     * @return
     */
    @SuppressWarnings("unchecked")
    private T[] castArray(byte[] data) {
        T[] result = (T[]) Array.newInstance(elementClass, data.length);
        for (int i = 0; i < data.length; i++)
            result[i] = DataParser.cast(new Byte(data[i]), elementClass);
        return result;
    }

    /**
     * Cast an array of primitives
     * @param data
     * @return
     */
    @SuppressWarnings("unchecked")
    private T[] castArray(double[] data) {
        T[] result = (T[]) Array.newInstance(elementClass, data.length);
        for (int i = 0; i < data.length; i++)
            result[i] = DataParser.cast(new Double(data[i]), elementClass);
        return result;
    }

    /**
     * Cast an array of primitives
     * @param data
     * @return
     */
    @SuppressWarnings("unchecked")
    private T[] castArray(float[] data) {
        T[] result = (T[]) Array.newInstance(elementClass, data.length);
        for (int i = 0; i < data.length; i++)
            result[i] = DataParser.cast(new Float(data[i]), elementClass);
        return result;
    }

    /**
     * Cast an array of primitives
     * @param data
     * @return
     */
    @SuppressWarnings("unchecked")
    private T[] castArray(char[] data) {
        T[] result = (T[]) Array.newInstance(elementClass, data.length);
        for (int i = 0; i < data.length; i++)
            result[i] = DataParser.cast(new Character(data[i]), elementClass);
        return result;
    }

    /**
     * Cast an array of primitives
     * @param data
     * @return
     */
    @SuppressWarnings("unchecked")
    private T[] castArray(boolean[] data) {
        T[] result = (T[]) Array.newInstance(elementClass, data.length);
        for (int i = 0; i < data.length; i++)
            result[i] = DataParser.cast(new Boolean(data[i]), elementClass);
        return result;
    }

    /*
     * @see za.co.softco.text.Parser#cast(java.lang.Object)
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public T[] cast(Object value) {
        if (value == null)
            return null;

        value = DataParser.unwrapJavascriptObject(value);
        
        if (value instanceof java.sql.Array)
            try {
                value = ((java.sql.Array) value).getArray();
            } catch (SQLException e) {
                throw new ClassCastException(e.getMessage());
            }
        
        if (value instanceof Map)
            value = ((Map) value).entrySet();

        if (value instanceof Collection)
            value = ((Collection) value).toArray();

        if (value instanceof Appendable)
            value = value.toString();

        if (value instanceof String && value.getClass() != elementClass) {
            try {
                return parse(value.toString());
            } catch (ParseException e) {
                throw new ClassCastException("Could not parse string as " + elementClass.getName() + "[]");
            }
        }

        if (!value.getClass().isArray()) {
            T[] result = (T[]) Array.newInstance(elementClass, 1);
            result[0] = DataParser.cast(value, elementClass);
            return result;
        }

        if (!value.getClass().getComponentType().isPrimitive()) {
            Object[] temp = (Object[]) value;
            T[] result = (T[]) Array.newInstance(elementClass, temp.length);
            for (int i = 0; i < result.length; i++)
                result[i] = DataParser.cast(temp[i], elementClass);
            return result;
        }

        if (value instanceof int[])
            return castArray((int[]) value);
        if (value instanceof long[])
            return castArray((long[]) value);
        if (value instanceof short[])
            return castArray((short[]) value);
        if (value instanceof byte[])
            return castArray((byte[]) value);
        if (value instanceof float[])
            return castArray((float[]) value);
        if (value instanceof double[])
            return castArray((double[]) value);
        if (value instanceof char[])
            return castArray((char[]) value);
        if (value instanceof boolean[])
            return castArray((boolean[]) value);

        T[] result = (T[]) Array.newInstance(elementClass, 1);
        result[0] = DataParser.cast(value, elementClass);
        return result;
    }
}
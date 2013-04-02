/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: GenericParser.java,v $
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
 *  Revision 1.4  2006/03/29 07:14:18  goofyxp
 *  Use generics functions to avoid type casting
 *
 *  Revision 1.3  2006/03/18 19:53:25  obelix
 *  Restructure code to use Map<String, Object> in stead of Properties
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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.apache.log4j.Logger;

/**
 * This parser is used when no specifc parser is defined for a class of objects
 * @author John Bester
 * @model
 */
class GenericParser<T> implements Parser<T> {
    private static final Class<?>[] CONSTRUCTOR_PARAMETERS = new Class[] { String.class };
    private final Constructor<T> constructor;

    /*
     * @see za.co.softco.text.Parser#allowMultiItemPrecast()
     */
    @Override
    public boolean allowMultiItemPrecast() {
        return true;
    }

    /**
     * Constructor of the GenericParser class
     * @param dataClass - Class which must be created when a string is parsed
     * @throws InstantiationException
     */
    public GenericParser(Class<T> dataClass) throws InstantiationException {
        try {
            constructor = dataClass.getConstructor(CONSTRUCTOR_PARAMETERS);
        } catch (NoSuchMethodException e) {
            throw new InstantiationException(dataClass.getName() + "(String) constructor required. Parser." + dataClass.getName() + "Parser class must be implemented.");
        }
    }

    /*
     * @see za.co.softco.io.DataParser#parseValue(java.lang.String)
     */
    @Override
    public T parse(String value) {
        try {
            return constructor.newInstance(new Object[] { value });
        } catch (IllegalAccessException e) {
            Logger.getLogger(GenericParser.class).error(e);
        } catch (InvocationTargetException e) {
            Logger.getLogger(GenericParser.class).error(e);
        } catch (InstantiationException e) {
            Logger.getLogger(GenericParser.class).error(e);
        }
        return null;
    }

    /*
     * @see za.co.softco.parser.Parser#toString(java.lang.Object)
     */
    @Override
    public String toString(Object value) {
        return (value != null ? value.toString() : null);
    }

    /*
     * @see za.co.softco.parser.Parser#format(java.lang.Object, java.lang.String)
     */
    @Override
    public String format(Object value, String format) {
        throw new IllegalStateException("Not yet implemented");
    }

    /*
     * @see za.co.softco.text.Parser#cast(java.lang.Object)
     */
    @Override
    public T cast(Object value) {
        return constructor.getDeclaringClass().cast(value);
    }
}
/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: DimensionedConstructor.java,v $
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
 *  Revision 1.2  2005/12/05 09:16:50  obelix
 *  Add comments
 *
 *  Created on 01-Dec-2005
 *******************************************************************************/
package za.co.softco.text;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;

/**
 * This class is used to represent a constructor of a class that takes a sequence of numeric values
 * @author john
 * @model
 */
public class DimensionedConstructor<T> {
    private final Constructor<? extends T> con;

    public DimensionedConstructor(Constructor<? extends T> con) {
        this.con = con;
    }

    /*
     * @see za.co.softco.parser.NumberArrayParser#parse(java.lang.Class, java.lang.Class, java.lang.String[])
     */
    protected T parse(Class<? extends T> resultClass, Class<? extends Number> numberClass, String[] values) throws ParseException {
        Class<?>[] types = con.getParameterTypes();
        Object[] params = new Object[types.length];
        for (int i = 0; i < params.length; i++)
            params[i] = DataParser.parse(values[i], types[i]);
        try {
            return con.newInstance(params);
        } catch (IllegalArgumentException e) {
            throw new ParseException(e.getMessage(), 0);
        } catch (InstantiationException e) {
            throw new ParseException(e.getMessage(), 0);
        } catch (IllegalAccessException e) {
            throw new ParseException(e.getMessage(), 0);
        } catch (InvocationTargetException e) {
            throw new ParseException(e.getMessage(), 0);
        }
    }

    /*
     * @see za.co.softco.parser.Parser#format(java.lang.Object, java.lang.String)
     */
    public String format(Object value, String format) {
        throw new IllegalStateException("Not yet implemented");
    }
}

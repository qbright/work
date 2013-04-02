/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: DimensionedParser.java,v $
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
 *  Revision 1.1  2006/03/18 19:53:25  obelix
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
 *  Revision 1.2  2005/12/05 09:18:27  obelix
 *  Add comments
 *  Implement new format(Object, String) function in Parser interface to throw a default exception
 *
 *  Created on 01-Dec-2005
 *******************************************************************************/
package za.co.softco.text;

import java.awt.Dimension;
import java.awt.geom.Dimension2D;
import java.lang.reflect.Constructor;
import java.text.ParseException;

/**
 * Parse / format dimension objects
 * @author john
 * @model
 */
public abstract class DimensionedParser<Type> extends NumberArrayParser<Type> {

    /*
     * @see za.co.softco.parser.NumberArrayParser#parse(java.lang.Class, java.lang.Class, java.lang.String[])
     */
    @Override
    protected Type parse(Class<? extends Type> resultClass, Class<? extends Number> numberClass, String[] values) throws ParseException {
        Constructor<? extends Type> con = null;

        if (resultClass == null)
            throw new ParseException("No result class specified", 0);

        try {
            con = getPreferredConstructor(resultClass, values.length);
        } catch (NoSuchMethodException e) {
            // Ignore exception
        }

        if (con == null) {
            try {
                con = getContructor(resultClass, getPreferredNumberClasses(values));
            } catch (SecurityException e) {
                throw new ParseException(e.getMessage(), 0);
            } catch (NoSuchMethodException e) {
                throw new ParseException(e.getMessage(), 0);
            }
        }

        return new DimensionedConstructor<Type>(con).parse(resultClass, null, values);
    }

    /*
     * @see za.co.softco.parser.Parser#toString(java.lang.Object)
     */
    @Override
    public String toString(Object value) {
        if (value == null)
            return null;

        if (value instanceof Dimension)
            return "[w=" + ((Dimension) value).width + ",h=" + ((Dimension) value).height + "]";
        if (value instanceof Dimension2D)
            return "[w=" + ((Dimension2D) value).getWidth() + ",h=" + ((Dimension2D) value).getHeight() + "]";
        return value.toString();
    }

    /*
     * @see za.co.softco.parser.Parser#format(java.lang.Object, java.lang.String)
     */
    @Override
    public String format(Object value, String format) {
        throw new IllegalStateException("Not yet implemented");
    }

}

/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: PointParser.java,v $
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
 *  Revision 1.2  2005/12/05 09:18:56  obelix
 *  Add comments
 *
 *  Created on 01-Dec-2005
 *******************************************************************************/
package za.co.softco.text;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.lang.reflect.Constructor;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;

/**
 * Parse / format points
 * @author john
 * @model
 */
public class PointParser extends NumberArrayParser<Point2D> {
    private static final Map<Class<? extends Number>, Class<? extends Point2D>> classes = new HashMap<Class<? extends Number>, Class<? extends Point2D>>();

    static {
        classes.put(Integer.class, Point.class);
        classes.put(Integer.TYPE, Point.class);
        classes.put(Float.class, Point2D.Float.class);
        classes.put(Float.TYPE, Point2D.Float.class);
    }

    /*
     * @see za.co.softco.text.NumberArrayParser#parse(java.lang.Class, java.lang.Class, java.lang.String[])
     */
    @Override
    public boolean allowMultiItemPrecast() {
        return true;
    }

    @Override
    protected Point2D parse(Class<? extends Point2D> resultClass, Class<? extends Number> numberClass, String[] values) throws ParseException {
        Constructor<? extends Point2D> con = null;

        if (resultClass == null) {
            resultClass = classes.get(getPreferredNumberClass(values));
            if (resultClass == null)
                resultClass = Point2D.Double.class;
        }

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

        return new DimensionedConstructor<Point2D>(con).parse(resultClass, null, values);
    }

    /*
     * @see za.co.softco.text.NumberArrayParser#toString(java.lang.Object)
     */
    @Override
    public String toString(Object value) {
        if (value == null)
            return null;

        if (value instanceof Point)
            return "[x=" + ((Point) value).x + ",y=" + ((Point) value).y + "]";
        if (value instanceof Point2D)
            return "[x=" + ((Point2D) value).getX() + ",y=" + ((Point2D) value).getY() + "]";
        return value.toString();
    }

    /*
     * @see za.co.softco.text.Parser#cast(java.lang.Object)
     */
    @Override
    public Point2D cast(Object value) {
        try {
            return Point2D.class.cast(value);
        } catch (ClassCastException e) {
            try {
                return parse(DataParser.format(value));
            } catch (ParseException e1) {
                throw new ClassCastException(e1.getMessage());
            }
        }
    }
}

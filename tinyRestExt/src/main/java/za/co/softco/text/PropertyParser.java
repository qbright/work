/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: PropertyParser.java,v $
 *  Revision 1.5  2007/12/23 17:02:17  remjohn
 *  Fixed comments
 *
 *  Revision 1.4  2007/12/22 19:35:14  remjohn
 *  Improve DataParser framework
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
 *  Revision 1.2  2006/09/13 10:15:38  obelix
 *  Improve functionality
 *
 *  Revision 1.1  2006/09/13 09:31:14  obelix
 *  Created
 *
 *  Created on September 13, 2006
 *******************************************************************************/
package za.co.softco.text;

import java.beans.IntrospectionException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.util.Map;

import za.co.softco.bean.DefaultBeanManager;
import za.co.softco.util.PropertyMap;
import za.co.softco.util.Utils;

/**
 * Parser to handle maps with String keys
 * @author John Bester
 * @model
 */
public class PropertyParser implements Parser<Map<String, Object>> {

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
    public Map<String, Object> parse(String value) throws ParseException {
        if (value == null)
            return null;

        if (!value.startsWith("{"))
            throw new ParseException("{ expected", 0);

        if (!value.endsWith("}"))
            throw new ParseException("} expected", value.length());

        Map<String, Object> result = new PropertyMap<Object>();
        int ndx = 1;
        for (String prop : value.substring(1, value.length() - 1).split(",")) {
            if (Utils.normalize(prop) == null) {
                ndx += prop.length() + 1;
                continue;
            }
            int pos = prop.indexOf("=");
            if (pos == 0)
                throw new ParseException("No property name", ndx);
            if (pos < 0)
                result.put(Utils.normalize(prop), null);
            else
                result.put(Utils.normalize(prop.substring(0, pos)), Utils.normalize(prop.substring(pos + 1)));
        }
        return result;
    }

    /*
     * @see za.co.softco.parser.Parser#toString(java.lang.Object)
     */
    @Override
    @SuppressWarnings("unchecked")
    public String toString(Object value) {
        if (value == null)
            return null;
        if (value instanceof Map) {
            StringBuilder result = new StringBuilder("{");
            for (Map.Entry<Object, Object> e : ((Map<Object, Object>) value).entrySet()) {
                if (result.length() > 1)
                    result.append(",");
                result.append(DataParser.format(e.getKey()));
                result.append("=");
                Object val = e.getValue();
                if (val != null)
                    result.append(DataParser.format(val));
            }
            result.append("}");
            return result.toString();
        }
        return value.toString();
    }

    /*
     * @see za.co.softco.parser.Parser#format(java.lang.Object, java.lang.String)
     */
    @Override
    public String format(Object value, String format) {
        return toString(value);
    }

    /*
     * @see za.co.softco.text.Parser#cast(java.lang.Object)
     */
    @Override
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Map<String, Object> cast(Object value) {
        if (value == null)
            return null;

        if (value.getClass().isArray())
            value = Array.get(value, 0);

        if (value instanceof Map) {
            boolean ok = true;
            for (Object key : ((Map) value).values()) {
                if (!(key instanceof String)) {
                    ok = false;
                    break;
                }
            }
            if (ok)
                return (Map<String, Object>) value;

            Map<String, Object> result = new PropertyMap<Object>();
            for (Map.Entry<Object, Object> e : ((Map<Object, Object>) value).entrySet())
                result.put(e.getKey().toString(), e.getValue());
            return result;
        }

        if (value instanceof String)
            try {
                return parse((String) value);
            } catch (ParseException e) {
                throw new ClassCastException(e.getMessage() + " at " + e.getErrorOffset());
            }

        try {
            return new DefaultBeanManager(value).getProperties();
        } catch (IntrospectionException e) {
            throw new ClassCastException("Could not cast " + value.getClass().getName() + " to Map<String,Object");
        }
    }
}
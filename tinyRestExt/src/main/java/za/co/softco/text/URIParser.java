/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: DoubleParser.java,v $
 *  Revision 1.4  2007/12/23 17:02:17  remjohn
 *  Fixed comments
 *
 *  Revision 1.3  2007/12/22 19:35:14  remjohn
 *  Improve DataParser framework
 *
 *  Revision 1.2  2007/09/07 09:29:34  remjohn
 *  Apply changes from bester package
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
 *  Revision 1.7  2007/03/14 16:29:38  goofyxp
 *  Improve parsing
 *
 *  Revision 1.6  2006/12/17 10:59:52  goofyxp
 *  Use Number.doubleValue() in stead of Number.floatValue()
 *
 *  Revision 1.5  2006/12/13 06:40:24  obelix
 *  Improve parsing of numeric values to handle default number format (thousand separator)
 *
 *  Revision 1.4  2006/09/29 11:18:34  obelix
 *  Implemented format(Object, String)
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

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;

import za.co.softco.util.Utils;

/**
 * Parse / format double values
 * @author john
 * @model
 */
public class URIParser implements Parser<URI> {
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
    public URI parse(String value) throws ParseException {
        value = Utils.normalize(value);
        if (value == null)
            return null;
        try {
            return new URI(value);
        } catch (URISyntaxException e) {
            throw Utils.cast(e, ParseException.class);
        }
    }

    /*
     * @see za.co.softco.parser.Parser#toString(java.lang.Object)
     */
    @Override
    public String toString(Object value) {
        if (value instanceof URI)
            return ((URI) value).toString();
        if (value instanceof URL)
            return ((URL) value).toString();
        return (value != null ? value.toString() : null);
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
    public URI cast(Object value) {
        if (value instanceof URI)
            return (URI) value;
        try {
            if (value instanceof URL)
                return ((URL) value).toURI();
            return parse(value.toString());
        } catch (ParseException e) {
            throw new ClassCastException(e.getMessage());
        } catch (URISyntaxException e) {
            throw new ClassCastException(e.getMessage());
        }
    }
}
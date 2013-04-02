/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author     John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: IntegerParser.java,v $
 *  Revision 1.4  2008/01/14 15:48:21  remjohn
 *  Refactor
 *
 *  Revision 1.3  2007/12/22 19:35:14  remjohn
 *  Improve DataParser framework
 *
 *  Revision 1.2  2007/08/20 19:10:22  rembrink
 *  Changes to handle Integer and Boolean conversions (By John)
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
 *  Revision 1.6  2007/04/07 17:03:28  goofyxp
 *  Use valueOf() in stead of new to minimize memory usage
 *
 *  Revision 1.5  2006/12/13 06:40:24  obelix
 *  Improve parsing of numeric values to handle default number format (thousand separator)
 *
 *  Revision 1.4  2006/06/15 07:40:40  hugo
 *  Changes made
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

import java.io.File;
import java.text.ParseException;

import za.co.softco.util.Utils;

/**
 * Parse / format integer values
 * @author john
 * @model
 */
public class FileParser implements Parser<File> {

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
    public File parse(String value) throws ParseException {
        value = Utils.normalize(value);
        return (value != null ? new File(value) : null);
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
    public File cast(Object value) {
        if (value instanceof File)
            return (File) value;
        if (value instanceof String || value instanceof Appendable)
        	return new File(value.toString());
        throw new ClassCastException("Cannot convert " + value.getClass().getName() + " to a " + File.class.getName());
    }
}
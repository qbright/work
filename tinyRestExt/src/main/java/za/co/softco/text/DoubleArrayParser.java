/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Revision 1.2  2007/12/22 19:35:14  remjohn
 *  Improve DataParser framework
 *
 *  Revision 1.1  2007/10/11 11:43:52  remjohn
 *  Added to CVS
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
 *  Revision 1.3  2006/12/28 17:45:21  obelix
 *  Fix bug
 *
 *  Revision 1.2  2006/12/28 15:34:11  goofyxp
 *  Use java.reflect.Array.newInstance() in stead of factory
 *
 *  Revision 1.1  2006/09/01 14:23:24  obelix
 *  Created
 *
 *  Created on Sep 1, 2006
 *******************************************************************************/
package za.co.softco.text;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Arrays;

/**
 * Parser class used to handle int[] objects
 * @author john
 * @model
 */
public class DoubleArrayParser implements Parser<double[]> {

    /**
     * Default contructor
     */
    public DoubleArrayParser() {
        super();
    }

    /*
     * @see za.co.softco.text.Parser#allowMultiItemPrecast()
     */
    @Override
    public boolean allowMultiItemPrecast() {
        return true;
    }

    /*
     * @see za.co.softco.text.Parser#parse(java.lang.String)
     */
    @Override
    public double[] parse(String value) throws ParseException {
        if (value == null || value.length() == 0)
            return new double[0];
        return cast(new ArrayParser<Double>(Double.class).parse(value));
    }

    /*
     * @see za.co.softco.text.Parser#format(java.lang.Object, java.lang.String)
     */
    @Override
    public String format(Object value, String format) {
        double[] values = cast(value);
        if (values == null || values.length == 0)
            return "[]";

        if (format == null)
            return toString(value);

        DecimalFormat fmt = new DecimalFormat(format);
        StringBuilder result = new StringBuilder("[");
        for (int i = 0; i < values.length; i++) {
            if (result.length() > 1)
                result.append(",");
            result.append(fmt.format(values[i]));
        }
        result.append("]");
        return result.toString();
    }

    /*
     * @see za.co.softco.text.Parser#toString(java.lang.Object)
     */
    @Override
    public String toString(Object value) {
        return Arrays.toString(cast(value));
    }

    /*
     * @see za.co.softco.text.Parser#cast(java.lang.Object)
     */
    @Override
    public double[] cast(Object value) {
        if (value == null)
            return new double[0];
        if (value instanceof double[])
            return (double[]) value;
        if (value instanceof float[]) {
            float[] tmp = (float[]) value;
            double[] result = new double[tmp.length];
            for (int i = 0; i < result.length; i++)
                result[i] = tmp[i];
            return result;
        }
        Double[] temp = new ArrayParser<Double>(Double.class).cast(value);
        if (temp == null || temp.length == 0)
            return new double[0];
        double[] result = new double[temp.length];
        for (int i = 0; i < result.length; i++)
            result[i] = (temp[i] != null ? temp[i].longValue() : 0);
        return result;
    }

}

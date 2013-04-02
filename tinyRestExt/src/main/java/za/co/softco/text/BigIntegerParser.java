/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * 
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log: BigIntegerParser.java,v $
 *  Revision 1.3  2007/12/23 17:02:17  remjohn
 *  Fixed comments
 *
 *  Revision 1.2  2007/12/22 19:35:14  remjohn
 *  Improve DataParser framework
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
 *  Revision 1.2  2006/12/13 06:40:24  obelix
 *  Improve parsing of numeric values to handle default number format (thousand separator)
 *
 *  Revision 1.1  2006/09/29 11:18:02  obelix
 *  Created
 *
 *  Created on 29-Sep-2006
 *******************************************************************************/
package za.co.softco.text;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;

/**
 * Parse / format double values
 * @author john
 * @model
 */
public class BigIntegerParser implements Parser<BigInteger> {

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
    public BigInteger parse(String value) throws ParseException {
        value = DoubleParser.normalizeDouble(value);
        if (value == null)
            return null;
        try {
            return new BigInteger(value);
        } catch (NumberFormatException e) {
            try {
                return BigInteger.valueOf(new BigDecimal(value).longValue());
            } catch (NumberFormatException e2) {
                Number result = NumberFormat.getIntegerInstance().parse(value);
                return (result != null ? new BigInteger(result.toString()) : null);
            }
        }
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
        return new DecimalFormat(format).format(value);
    }

    /*
     * @see za.co.softco.text.Parser#cast(java.lang.Object)
     */
    @Override
    public BigInteger cast(Object value) {
        if (value instanceof BigInteger || value == null)
            return (BigInteger) value;
        if (value instanceof Number)
            return new BigInteger(value.toString());
        try {
            return BigInteger.class.cast(value);
        } catch (ClassCastException e) {
            try {
                return parse(value.toString());
            } catch (ParseException e1) {
                throw new ClassCastException(e1.getMessage());
            }
        }
    }
}
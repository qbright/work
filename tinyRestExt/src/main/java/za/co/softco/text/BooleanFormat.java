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
 *  Revision 1.1  2007/09/07 07:17:03  remjohn
 *  Added to CVS
 *
 *  Created on 11 Aug 2007
 *******************************************************************************/
package za.co.softco.text;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 * Different format objects to format booleans with
 * @author john
 * @model
 */
public abstract class BooleanFormat extends Format {
    private static final long serialVersionUID = 1988090136363264340L;

    /*
     * @see java.text.Format#parseObject(java.lang.String, java.text.ParsePosition)
     */
    @Override
    public Object parseObject(String source, ParsePosition pos) {
        String temp = source.substring(pos.getIndex());
        pos.setIndex(source.length());
        return DataParser.cast(temp, Boolean.class);
    }

    /**
     * Represent a boolean with 1 or 0
     * @author john
     * @model
     */
    public static class Numeric extends BooleanFormat {
        private static final long serialVersionUID = -164382484146118524L;

        /*
         * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
         */
        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            boolean value = BooleanParser.toBoolean(obj);
            toAppendTo.append(value ? "1" : "0");
            return toAppendTo;
        }
    }

    /**
     * Represent a boolean with True or False
     * @author john
     * @model
     */
    public static class TrueFalse extends BooleanFormat {
        private static final long serialVersionUID = -557927331558890045L;

        /*
         * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
         */
        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            boolean value = BooleanParser.toBoolean(obj);
            toAppendTo.append(value ? "True" : "False");
            return toAppendTo;
        }
    }

    /**
     * Represent a boolean with Yes or No
     * @author john
     * @model
     */
    public static class YesNo extends BooleanFormat {
        private static final long serialVersionUID = 4688078717981026394L;

        /*
         * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
         */
        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            boolean value = BooleanParser.toBoolean(obj);
            toAppendTo.append(value ? "True" : "False");
            return toAppendTo;
        }
    }

    /**
     * Represent a boolean with T or F
     * @author john
     * @model
     */
    public static class TF extends BooleanFormat {

        private static final long serialVersionUID = 8787462445887918051L;

        /*
         * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
         */
        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            boolean value = BooleanParser.toBoolean(obj);
            toAppendTo.append(value ? "T" : "F");
            return toAppendTo;
        }
    }

    /**
     * Represent a boolean with Y or N
     * @author john
     * @model
     */
    public static class YN extends BooleanFormat {

        private static final long serialVersionUID = 6196690460909801822L;

        /*
         * @see java.text.Format#format(java.lang.Object, java.lang.StringBuffer, java.text.FieldPosition)
         */
        @Override
        public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
            boolean value = BooleanParser.toBoolean(obj);
            toAppendTo.append(value ? "Y" : "N");
            return toAppendTo;
        }
    }
}

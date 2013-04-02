/*******************************************************************************
 *              Copyright (C) Bester Consulting 2005. All Rights reserved.
 * @author      John Bester
 * Project:     Library
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 15 Jun 2012
 *******************************************************************************/
package za.co.softco.text.model;

import java.text.ParseException;

/**
 * Exception thrown when no parser is defined
 * @author john
 */
public class NoParserException extends ParseException {
	private static final long serialVersionUID = -8652469216632101584L;

	public NoParserException(Class<?> clazz, int offset) {
        super("No parser for class " + (clazz != null ? clazz.getName() : "NULL"), offset);
    }

    public NoParserException(Class<?> clazz) {
        this(clazz, 0);
    }
}

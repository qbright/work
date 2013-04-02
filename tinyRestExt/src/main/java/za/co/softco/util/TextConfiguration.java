/*******************************************************************************
 * Copyright (C) Bester Consulting 2010. All Rights reserved.
 * This file may be distributed under the Softco / L-Mobile Share License
 * 
 * @author      John Bester
 * Project:     Library 
 * Description: Library classes
 *
 * Changelog  
 *  $Log$
 *  Created on 09 Feb 2011
 *******************************************************************************/
package za.co.softco.util;

import java.text.ParseException;

/**
 * Implement this interface for an
 * @author john
 */
public interface TextConfiguration {

    /**
     * Set configuration element text 
     * @param text
     * @throws ParseException
     */
    public void setText(String text) throws ParseException;
}

/*******************************************************************************
 *              Copyright (C) Bester Consulting 2010. All Rights reserved.
 * @author      John Bester
 * Project:     SoftcoRest
 * Description: HTTP REST Server
 *
 * Changelog  
 *  $Log$
 *  Created on 17 Feb 2012
 *******************************************************************************/
package za.co.softco.rest.model;

/**
 * Annotation to indicate that a function can process HTTP POST
 * (Not used yet)
 * @author john
 */
public @interface Post {
    public String value();
}

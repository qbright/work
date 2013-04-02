/*******************************************************************************
 * Copyright (C) Bester Consulting 2010. All Rights reserved.
 * This file may be distributed under the Softco Share License
 * 
 * @author      John Bester
 * Project:     SoftcoRest
 * Description: HTTP REST Server
 *
 * Changelog  
 *  $Log$
 *  Created on 06 Jun 2010
 *******************************************************************************/
package za.co.softco.rest;

import za.co.softco.rest.model.Context;

/**
 * Implement this method to authenticate an HTTP request
 * @author john
 */
public interface AuthenticationListener {

    /**
     * Used by RestRequest.authenticate()
     * @param request
     * @param username
     * @param password
     * @throws SecurityException
     */
    public void authenticate(Context request, String username, String password) throws SecurityException;
}

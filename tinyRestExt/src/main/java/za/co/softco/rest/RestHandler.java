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
 *  Created on 19 Nov 2009
 *******************************************************************************/
package za.co.softco.rest;

import java.util.Map;

import za.co.softco.rest.model.Context;


/**
 * Interface from which different REST handlers can be created. Currently
 * there is an implementation that uses reflection information to build
 * method handlers. Ideally an implementation should be added that uses 
 * annotations to determine available REST functions.
 * @author john
 */
public interface RestHandler {

	/**
	 * Return the name of the service
	 * @return
	 */
	public String getServiceName();
	
	/**
	 * Handle the Rest request. The event contains methods to build the reply
	 * @param request
	 * @throws Exception
	 */
	public void handle(Context request) throws Exception;
	
	
	/**
	 * Return the versions of all services handled by this handler
	 * @return
	 */
	public Map<String,String> getServiceVersions();
}

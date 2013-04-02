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

import static za.co.softco.rest.http.HttpConstants.HTTP_BAD_REQUEST;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;

import za.co.softco.util.Utils;

/**
 * Register your REST handlers by calling the register(RestHandler) method
 * on this class. This class then produces the proper handler for a given
 * HTTP request.
 * @author john
 */
public class RestFactory {

	private final Map<String,RestHandler> handlers = new HashMap<String,RestHandler>();
	
	/**
	 * Register a REST handler with a specific HTTP path
	 * @param path
	 * @param handler
	 * @return
	 */
	public RestHandler register(String path, RestHandler handler) {
		if (path == null)
			path = "";
		if (path.startsWith("/"))
			path = path.substring(1);
		handlers.put(path, handler);
		return handler;
	}

	/**
     * Register a REST handler. The service name is used as the path
	 * @param handler
	 * @return
	 */
	public RestHandler register(RestHandler handler) {
		return register(handler.getServiceName(), handler);
	}
	
	/**
	 * Return the right REST handler based on the HTTP path
	 * @param path
	 * @return
	 * @throws RestException
	 */
	public RestHandler getHandler(String path) throws RestException {
		if (path == null)
			path = "";
		if (path.startsWith("/"))
			path = path.substring(1);
		path = path.trim();
		RestHandler result = handlers.get(path.trim());
		if (result == null) {
			int pos = path.lastIndexOf('/');
			while (pos >= 0 && result == null) {
				result = handlers.get(path.substring(0,pos).trim());
				pos = path.lastIndexOf('/', pos-1);
			}
		}
		if (result == null)
			throw new RestException(HTTP_BAD_REQUEST, "Bad request: " + path);
		return result;
	}
	
	/**
	 * Return all REST handlers
	 * @return
	 */
    public RestHandler[] getHandlers() {
        return handlers.values().toArray(new RestHandler[handlers.size()]);
    }
    
    /**
     * Return the number of REST handlers
     * @return
     */
	public int getCount() {
		return handlers.size();
	}
	
	/**
	 * Return handler versions of all handlers by looking up manifests for the 
	 * jars to which the handler belong
	 * @return
	 */
	public Map<String,String> getHandlerVersions() {
	    Map<String,String> result = new LinkedHashMap<String,String>();
	    for (Map.Entry<String,RestHandler> handler : handlers.entrySet()) {
	        String name = "/" + handler.getValue().getServiceName();
            try {
                Manifest mf = Utils.getManifestByClass(handler.getValue().getClass());
                if (mf != null) {
                    String version = Utils.getVersion(mf, "unknown");
                    result.put(name, version);
                    continue;
                }
            } catch (IOException e) {
                Logger.getLogger(RestFactory.class).error("Could not get manifest for " + handler.getValue().getClass());
            }
            result.put(name, "unknown");
	    }
	    return result;
	}
}

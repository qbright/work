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
 *  Created on 20 Nov 2009
 *******************************************************************************/
package za.co.softco.rest;

import static za.co.softco.rest.http.HttpConstants.HTTP_BAD_REQUEST;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.jar.Manifest;

import org.apache.log4j.Logger;

import za.co.softco.rest.model.Context;
import za.co.softco.util.Utils;

/**
 * Handler that uses reflection to auto create REST methods. All public functions
 * in a class is mapped to REST functions.
 * @author john
 */
public class ReflectionHandler implements RestHandler {
	
	private final String serviceName;
	private final Map<String,ReflectionService> services = new HashMap<String,ReflectionService>(); 
	
	/**
	 * Constructor
	 * @param serviceName
	 */
	public ReflectionHandler(String serviceName) {
		if (serviceName == null)
			serviceName = "";
		if (serviceName.startsWith("/"))
			serviceName = serviceName.substring(1);
		this.serviceName = serviceName;
	}
	
	/*
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return serviceName.hashCode();
	}

	/*
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object ref) {
		if (!(ref instanceof ReflectionHandler))
			return false;
		return ((ReflectionHandler) ref).serviceName.equals(serviceName);
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return serviceName;
	}
	
	/*
	 * @see za.co.softco.rest.RestHandler#getServiceName()
	 */
	@Override
	public String getServiceName() {
		return serviceName;
	}
	
	/*
	 * @see za.co.softco.rest.RestHandler#handle(za.co.softco.rest.RestRequest)
	 */
	@Override
	public void handle(Context request) throws Exception {
		String path = request.getURL().getPath();
		if (path == null)
			throw new RestException(HTTP_BAD_REQUEST, "Service not found");
		if (path.startsWith("/"))
			path = path.substring(1);
		
		int pos = path.lastIndexOf('/');
		if (pos < 0 || pos >= path.length())
			throw new RestException(HTTP_BAD_REQUEST, "Service not found");

		String name = path.substring(0,pos);
		ReflectionService service = services.get(name);
		if (service == null && name.startsWith(serviceName)) {
			name = name.substring(serviceName.length());
			if (name.startsWith("/"))
				name = name.substring(1);
			service = services.get(name);
		}

		if (service != null) {
	        service.handle(request, path.substring(pos+1));
	        return;
		}
		
		int ndx;
		while ((ndx = name.indexOf('/')) >= 0) {
		    name = name.substring(0, ndx);
            service = services.get(name);
            if (service != null) {
                String method = path.substring(serviceName.length() + name.length() + 2);
                int split = method.indexOf('/');
                String item = null;
                if (split > 0) {
                    if (split < method.length()+1)
                        item = method.substring(split+1);
                    method = method.substring(0, split);
                    request.setItem(item);
                }
                service.handle(request, method);
                return;
            }
		}
		
		throw new RestException(HTTP_BAD_REQUEST, "Service not found");
	}

	/**
	 * Register a service
	 * @param service
	 * @return
	 */
	public ReflectionService register(ReflectionService service) {
		services.put(service.toString(), service);
		return service;
	}

	/*
	 * @see za.co.softco.rest.RestHandler#getServiceVersions()
	 */
    @Override
    public Map<String,String> getServiceVersions() {
        Map<String,String> result = new LinkedHashMap<String,String>();
        for (Map.Entry<String,ReflectionService> service : services.entrySet()) {
            String version;
            try {
                Manifest mf = Utils.getManifestByClass(service.getValue().getClass());
                version = Utils.getVersion(mf, "unknown");
            } catch (IOException e) {
                Logger.getLogger(ReflectionHandler.class).error("Could not load manifest for " + service.getValue().getClass().getName());
                version = "unknown";
            }
            result.put(service.getKey(), version);
        }
        return result;
    }
}

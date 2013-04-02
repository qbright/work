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
import java.util.Properties;

/**
 * Default REST factory
 * @author john
 * @see za.co.softco.rest.RestFactory
 */
public class DefaultRestFactory extends RestFactory {

    /**
     * Constructor
     * @param props
     */
	public DefaultRestFactory(Properties props) {
		if (props != null)
			load(props);
	}

	/**
	 * Default constructor
	 */
	public DefaultRestFactory() {
		this(null);
	}

	/**
	 * Register handlers from some service=classname configuration.
	 * @param props
	 */
	public void load(Properties props) {
		for (Map.Entry<Object,Object> prop : props.entrySet()) {
			if (!(prop.getKey() instanceof String))
				continue;
			String key = (String) prop.getKey();
			Object val = prop.getValue();
			if (val == null)
				continue;
			
			if (val instanceof String) {
				try {
					val = Class.forName((String) val);
				} catch (ClassNotFoundException e) {
					continue;
				}
			}
			
			if (val instanceof Class<?>) {
				try {
					val = ((Class<?>) val).newInstance();
				} catch (InstantiationException e) {
					continue;
				} catch (IllegalAccessException e) {
					continue;
				}
			}

			if (val instanceof RestHandler)
				register(key, (RestHandler) val);
		}
	}
}

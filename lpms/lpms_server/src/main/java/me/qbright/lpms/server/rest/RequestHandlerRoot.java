package me.qbright.lpms.server.rest;

import me.qbright.lpms.common.ConfigCommon;

import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 * @author QBRIGHT
 * @date 2013-4-5
 * 
 */
public class RequestHandlerRoot extends Application{

	/* (non-Javadoc)
	 * @see org.restlet.Application#createInboundRoot()
	 */
	@Override
	public Restlet createInboundRoot() {
		// TODO Auto-generated method stub
		Router router = new Router(getContext());
		router = RestletServer.bindServerResource(router, ConfigCommon.getKeyString("PACKAGE_PATH"));
		return router; 
	}

	
}

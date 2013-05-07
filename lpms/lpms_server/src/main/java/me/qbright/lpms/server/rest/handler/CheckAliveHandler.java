package me.qbright.lpms.server.rest.handler;

import me.qbright.lpms.server.rest.RequestHandler;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * @author QBRIGHT
 * @date 2013-5-15
 */
@RequestHandler(serverName="/checkAlive")
public class CheckAliveHandler extends ServerResource implements BaseHandler{

	@Override
	@Get
	public String getContent() {
		return "true";
	}

}

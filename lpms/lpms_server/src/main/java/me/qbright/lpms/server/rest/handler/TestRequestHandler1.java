/**
 * @author qbright
 * 2013-4-5
 */
package me.qbright.lpms.server.rest.handler;

import me.qbright.lpms.server.rest.RequestHandler;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * @author QBRIGHT
 * @date 2013-4-5
 */
@RequestHandler(serverName="/nimei")
public class TestRequestHandler1 extends ServerResource{
	
	@Get
	public String hello(){
		return	"nimei" ;
	}
	
}

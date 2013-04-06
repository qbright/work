/**
 * @author qbright
 * 2013-4-5
 */
package me.qbright.lpms.server.rest.handler;

import java.io.IOException;

import me.qbright.lpms.server.rest.RequestHandler;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

/**
 * @author QBRIGHT
 * @date 2013-4-5
 */
@RequestHandler(serverName = "/hello")
public class TestRequestHandler extends ServerResource {

	@Post("json")
	public String hello() throws JsonGenerationException, JsonMappingException,
			IOException, SigarException {

		
	
		Sigar sigar = new Sigar();

		sigar.getCpuInfoList();

		ObjectMapper om = new ObjectMapper();

	
		return null;
	}

}

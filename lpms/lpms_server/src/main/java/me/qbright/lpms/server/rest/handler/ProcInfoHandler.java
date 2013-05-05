package me.qbright.lpms.server.rest.handler;

import java.io.IOException;

import me.qbright.lpms.server.rest.RequestHandler;
import me.qbright.lpms.server.service.ProcInfoService;

import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.resource.Get;
<<<<<<< HEAD
import org.restlet.resource.Post;
=======
>>>>>>> 0bfb3d37ba8fa4d731a5b25c3ea1baef4f5952a0
import org.restlet.resource.ServerResource;

/**
 * @author QBRIGHT
 * @date 2013-5-4
 */
@RequestHandler(serverName="/procInfo")
public class ProcInfoHandler extends ServerResource implements BaseHandler{
	
	private static ProcInfoService procInfoService = new ProcInfoService();
	@Override
	@Post
	public String getContent() {
		ObjectMapper om = new ObjectMapper();
		try {
			return om.writeValueAsString(procInfoService.getProcInfo());
		} catch (IOException e) {
			log.error("数据发送失败", e);
			return null;
		}		
	}

}

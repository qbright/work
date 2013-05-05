/**
 * @author qbright
 * 2013-4-30
 */
package me.qbright.lpms.server.rest.handler;

import java.io.IOException;

import me.qbright.lpms.server.rest.RequestHandler;
import me.qbright.lpms.server.service.NetInfoService;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

/**
 * @author QBRIGHT
 * @date 2013-4-30
 */
@RequestHandler(serverName = "/netInfo")
public class NetInfoHandler extends ServerResource implements BaseHandler {
	private static NetInfoService netInfoService = new NetInfoService();

	@Override
	@Post
	public String getContent() {
		ObjectMapper om = new ObjectMapper();
		try {
			return om.writeValueAsString(netInfoService.getNetInfo());
		} catch (IOException e) {
			log.error("数据发送失败", e);
			return null;
		}
	}

}

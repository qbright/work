package me.qbright.lpms.server.rest.handler;

import java.io.IOException;

import me.qbright.lpms.server.rest.RequestHandler;
import me.qbright.lpms.server.service.JavaEnvironmentService;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

/**
 * @author QBRIGHT
 * @date 2013-4-22
 */
@RequestHandler(serverName = "/javaEnvironment")
public class JavaEnvironmentHandler extends ServerResource implements
		BaseHandler {
	private static JavaEnvironmentService javaEnvironmentService = new JavaEnvironmentService();

	@Override
	@Get
	public String getContent() {
		try {
			return om.writeValueAsString(javaEnvironmentService.getJavaEvn());
		} catch (IOException e) {
			log.error("发送数据失败", e);
			return null;
		}
	}

}

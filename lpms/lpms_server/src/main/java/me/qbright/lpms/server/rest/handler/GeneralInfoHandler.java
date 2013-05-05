package me.qbright.lpms.server.rest.handler;

import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import me.qbright.lpms.server.rest.RequestHandler;
import me.qbright.lpms.server.service.GeneralInfoService;

/**
 * @author QBRIGHT
 * @date 2013-4-16
 */
@RequestHandler(serverName = "/generalInfo")
public class GeneralInfoHandler extends ServerResource implements BaseHandler {
	private GeneralInfoService generalInfoService = new GeneralInfoService();

	@Override
	@Post
	public String getContent() {
		try {
			return om.writeValueAsString(generalInfoService.getGeneralInfo());

		} catch (Exception e) {
			log.error("信息发送失败", e);
			return null;
		}
	}

}

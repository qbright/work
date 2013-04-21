/**
 * @author qbright
 * 2013-4-25
 */
package me.qbright.lpms.server.rest.handler;

import java.io.IOException;

import me.qbright.lpms.server.rest.RequestHandler;
import me.qbright.lpms.server.service.FileSystemInfoService;

import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

/**
 * @author QBRIGHT
 * @date 2013-4-25
 */
@RequestHandler(serverName = "/fileSystemInfo")
public class FileSystemInfoHandler extends ServerResource implements
		BaseHandler {
	private FileSystemInfoService fileSystemInfoService = new FileSystemInfoService();

	@Override
	@Get("json")
	public String getContent() {

		try {
			return om.writeValueAsString(fileSystemInfoService.getFileSys());
		} catch (IOException e) {
			log.error("信息发送失败", e);
			return null;
		}
	}

}

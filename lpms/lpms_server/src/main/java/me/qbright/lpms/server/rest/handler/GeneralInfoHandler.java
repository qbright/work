/**
 * @author qbright
 * 2013-4-16
 */
package me.qbright.lpms.server.rest.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import me.qbright.lpms.server.monitor.GeneralInfoMonitor;
import me.qbright.lpms.server.rest.RequestHandler;
import me.qbright.lpms.server.service.GeneralInfoService;

/**
 * @author QBRIGHT
 * @date 2013-4-16
 */
@RequestHandler(serverName = "/generalInfo")
public class GeneralInfoHandler extends ServerResource {
	private static Logger log = Logger.getLogger(GeneralInfoHandler.class);
	private GeneralInfoService generalInfoService = new GeneralInfoService();
	private static ObjectMapper om = new ObjectMapper();
	@Get("json")
	public String getGeneralInfo()  {
		try {
			return om.writeValueAsString(generalInfoService.getGeneralInfo());
		} catch (Exception e){
			log.error("信息发送失败", e);
			return null;
		}
		
	}
	
}

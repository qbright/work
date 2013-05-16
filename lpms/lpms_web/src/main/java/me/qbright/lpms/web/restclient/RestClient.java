package me.qbright.lpms.web.restclient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import me.qbright.lpms.web.entity.ServerMachine;
import me.qbright.lpms.web.monitor.MonitorUtil;

import org.apache.log4j.Logger;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Method;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author QBRIGHT
 * @date 2013-4-14
 */
public class RestClient {
	private static Logger log = Logger.getLogger(RestClient.class);

	private static ObjectMapper om = new ObjectMapper();

	private static final String HTTP = "http://";

	private static final String DOT = ":";

	private static final String MACHINE_NAME = "machineName";

	private static final String PASSWORD = "machinePassword";

	@SuppressWarnings("unchecked")
	public static Map<String, Object> getRestResponseAsMap(
			MonitorUtil monitorUtil, ServerMachine serverMachine) {
		ClientResource client = new ClientResource(Method.POST, getUrl(
				monitorUtil, serverMachine));
		Form form = new Form();

		form.add(MACHINE_NAME, serverMachine.getMachineName());
		form.add(PASSWORD, serverMachine.getPassword());
		try {
			String requestJson = client.post(
					form.getWebRepresentation().getText()).getText();

			 
			return om.readValue(requestJson,Map.class);
		} catch (ResourceException e) {
			log.error("获取系统信息出错   url ：" + getUrl(monitorUtil, serverMachine),
					e);
			return null;
		} catch (IOException e) {
			log.error("获取系统信息出错   url ：" + getUrl(monitorUtil, serverMachine),
					e);
			return null;
		}

	}
	
	public static Boolean getRestResponseAsBoolean(MonitorUtil monitorUtil, ServerMachine serverMachine){
		ClientResource client = new ClientResource(Method.POST, getUrl(
				monitorUtil, serverMachine));
		Form form = new Form();
		form.add(MACHINE_NAME, serverMachine.getMachineName());
		form.add(PASSWORD, serverMachine.getPassword());
		try {
			System.out.println(form.getWebRepresentation().getText());
			String requestJson = client.post(
					form.getWebRepresentation().getText()).getText();
			
			if(requestJson == null){
				throw new ResourceException(404);
			}
			return om.readValue(requestJson,Boolean.class);
		} catch (ResourceException e) {
			log.error("获取系统信息出错   url ：" + getUrl(monitorUtil, serverMachine),
					e);
			return false;
		} catch (IOException e) {
			log.error("获取系统信息出错   url ：" + getUrl(monitorUtil, serverMachine),
					e);
			return false;
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Map<String, Object>> getRestResponseAsList(
			MonitorUtil monitorUtil, ServerMachine serverMachine, String listKey) {

		Map<String, Object> map = getRestResponseAsMap(monitorUtil,
				serverMachine);

		try {
			return om.readValue((String) map.get(listKey), List.class);
		} catch (JsonParseException e) {
			log.error("获取系统信息出错   url ：" + getUrl(monitorUtil, serverMachine),
					e);
			return null;
		} catch (JsonMappingException e) {
			log.error("获取系统信息出错   url ：" + getUrl(monitorUtil, serverMachine),
					e);
			return null;
		} catch (IOException e) {
			log.error("获取系统信息出错   url ：" + getUrl(monitorUtil, serverMachine),
					e);
			return null;
		}

	}

	private static String getUrl(MonitorUtil monitorUtil,
			ServerMachine serverMachine) {
		return new StringBuffer().append(HTTP)
				.append(serverMachine.getConnection_ip()).append(DOT)
				.append(serverMachine.getConnection_port())
				.append(monitorUtil.getPath()).toString();
	}
	
	public static void main(String[] args) {
		ServerMachine serverMachine = new ServerMachine();
		
		serverMachine.setConnection_ip("127.0.0.1");
		serverMachine.setConnection_port("8081");
		
		
		Boolean alive = getRestResponseAsBoolean(MonitorUtil.CHECK_ALIVE, serverMachine);
		System.out.println(alive);
	}
}

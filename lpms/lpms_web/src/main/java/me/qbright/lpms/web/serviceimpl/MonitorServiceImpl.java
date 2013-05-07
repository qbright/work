/**
 * @author qbright
 *
 * @date 2013-2-7
 */
package me.qbright.lpms.web.serviceimpl;

import java.util.List;
import java.util.Map;

import me.qbright.lpms.web.entity.ServerMachine;
import me.qbright.lpms.web.monitor.MonitorUtil;
import me.qbright.lpms.web.restclient.RestClient;
import me.qbright.lpms.web.service.MonitorService;

import org.springframework.stereotype.Component;

@Component("monitorService")
public class MonitorServiceImpl implements MonitorService {


	@Override
	public Map<String, Object> getGeneralInfo(ServerMachine serverMachine) {
		return RestClient.getRestResponseAsMap(MonitorUtil.GENERAL_INFO, serverMachine);
	}

	@Override
	public Map<String, Object> getJavaEnvironment(ServerMachine serverMachine) {
		return RestClient.getRestResponseAsMap(MonitorUtil.JAVA_ENVIRONMENT, serverMachine);
	}

	@Override
	public List<Map<String, Object>> getFileSystemInfo(ServerMachine serverMachine) {
		return RestClient.getRestResponseAsList(MonitorUtil.FILESYSTEM_INFO, serverMachine, "fileSystem_list");
	}

	@Override
	public List<Map<String, Object>> getNetInfo(ServerMachine serverMachine) {
		return RestClient.getRestResponseAsList(MonitorUtil.NET_INFO, serverMachine, "netInfo_list");
	}

	@Override
	public List<Map<String, Object>> getProcInfo(ServerMachine serverMachine) {
		return RestClient.getRestResponseAsList(MonitorUtil.PROC_INFO, serverMachine, "procInfo_list");
	}

}

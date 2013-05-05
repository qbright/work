/**
 * @author qbright
 *
 * @date 2013-2-7
 */
package me.qbright.lpms.web.serviceimpl;

import java.util.Map;

import me.qbright.lpms.web.dao.ServerMachineDao;
import me.qbright.lpms.web.entity.ServerMachine;
import me.qbright.lpms.web.monitor.MonitorUtil;
import me.qbright.lpms.web.restclient.RestClient;
import me.qbright.lpms.web.service.MonitorService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("monitorService")
public class MonitorServiceImpl implements MonitorService {
	
	@Autowired
	private ServerMachineDao serverMachineDao;
	
	@Override
	public Map<String, Object> getGeneralInfo(ServerMachine serverMachine) {
		return RestClient.getRestResponseAsMap(MonitorUtil.GENERAL_INFO,
				serverMachine);
	}

}

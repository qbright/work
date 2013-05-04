/**
 * @author qbright
 *
 * @date 2013-2-7
 */
package me.qbright.lpms.web.serviceimpl;

import java.util.Map;

import me.qbright.lpms.web.entity.ServerMachine;
import me.qbright.lpms.web.service.MonitorService;

import org.springframework.stereotype.Component;

@Component("monitorService")
public class MonitorServiceImpl implements MonitorService {
	@Override
	public Map<String, String> getGeneralInfo(ServerMachine serverMachine) {
		// TODO Auto-generated method stub
		return null;
	}

}

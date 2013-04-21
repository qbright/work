package me.qbright.lpms.server.service;

import java.util.HashMap;
import java.util.Map;

import me.qbright.lpms.server.monitor.JavaEnvironmentMonitor;

/**
 * @author QBRIGHT
 * @date 2013-4-22
 */
public class JavaEnvironmentService {

	public Map<String, Object> getJavaEvn() {
		Map<String, Object> map = new HashMap<String, Object>();
		for (JavaEnvironmentMonitor jEm : JavaEnvironmentMonitor.values()) {
			map.put(jEm.getName(), jEm.getInfo());
		}
		return map;
	}
}

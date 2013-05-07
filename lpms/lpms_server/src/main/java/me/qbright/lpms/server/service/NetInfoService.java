/**
 * @author qbright
 * 2013-4-30
 */
package me.qbright.lpms.server.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import me.qbright.lpms.server.monitor.NetInfoMonitor;

/**
 * @author QBRIGHT
 * @date 2013-4-30
 */
public class NetInfoService {
	private Logger log = Logger.getLogger(NetInfoService.class);

	public Map<String, Object> getNetInfo() {
		ObjectMapper om = new ObjectMapper();
		Map<String, Object> map = new HashMap<String, Object>();
		for (NetInfoMonitor netInfoMonitor : NetInfoMonitor.values()) {
			try {
				map.put(netInfoMonitor.getName(), om.writeValueAsString(om
						.readValue(netInfoMonitor.getInfo(),
								netInfoMonitor.getReturnType())));
			} catch ( IOException e) {
				log.error("获取数据失败",e);
			}
		}
		return map;
	}
}

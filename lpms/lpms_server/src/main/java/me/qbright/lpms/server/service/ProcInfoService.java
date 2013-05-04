/**
 * @author qbright
 * 2013-5-4
 */
package me.qbright.lpms.server.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;



import me.qbright.lpms.server.monitor.ProcInfoMonitor;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * @author QBRIGHT
 * @date 2013-5-4
 */
public class ProcInfoService {
	private static Logger log = Logger.getLogger(ProcInfoService.class);
	
	public Map<String, Object> getProcInfo(){
		ObjectMapper om = new ObjectMapper();
		Map<String, Object> map = new HashMap<String, Object>();
		for (ProcInfoMonitor procInfoMonitor : ProcInfoMonitor.values()) {
			try {
				map.put(procInfoMonitor.getName(), om.writeValueAsString(om
						.readValue(procInfoMonitor.getInfo(),
								procInfoMonitor.getReturnType())));
			} catch ( IOException e) {
				log.error("获取数据失败",e);
			}
		}
		return map;
	}
}

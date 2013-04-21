package me.qbright.lpms.server.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import me.qbright.lpms.server.monitor.GeneralInfoMonitor;

/**
 * @author QBRIGHT
 * @date 2013-4-18
 */
public class GeneralInfoService {

	private static ObjectMapper om = new ObjectMapper();
	private static Logger log = Logger.getLogger(GeneralInfoService.class);

	public Map<String, Object> getGeneralInfo() {
		Map<String, Object> map = new HashMap<String, Object>();
		for (GeneralInfoMonitor gIm : GeneralInfoMonitor.values()) {
			if (gIm.getReturnType().equals(String.class)) {
				map.put(gIm.getName(), gIm.getInfo());
			} else {
				try {
					map.put(gIm.getName(),
							om.readValue(gIm.getInfo(), gIm.getReturnType()));
				} catch (IOException e) {
					log.error("数据转换失败", e);
					continue;
				}
			}
		}
		return map;
	}

}

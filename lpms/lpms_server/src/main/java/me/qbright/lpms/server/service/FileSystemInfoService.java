package me.qbright.lpms.server.service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import me.qbright.lpms.server.monitor.FileSystemInfoMonitor;

/**
 * @author QBRIGHT
 * @date 2013-4-24
 */
public class FileSystemInfoService {
	private Logger log = Logger.getLogger(FileSystemInfoService.class);

	public Map<String, Object> getFileSys() {
		ObjectMapper om = new ObjectMapper();
		Map<String, Object> map = new HashMap<String, Object>();
		for (FileSystemInfoMonitor fsm : FileSystemInfoMonitor.values()) {
			try {
				map.put(fsm.getName(),
						om.writeValueAsString(om.readValue(fsm.getInfo(),
								fsm.getReturnType())));
			} catch (IOException e) {
				log.error("数据转换失败", e);
			}
		}
		return map;
	}
}

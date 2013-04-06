/**
 * @author qbright
 * 2013-4-18
 */
package me.qbright.lpms.server.service;

import java.util.HashMap;
import java.util.Map;

import me.qbright.lpms.server.monitor.GeneralInfoMonitor;

/**
 * @author QBRIGHT
 * @date 2013-4-18
 */
public class GeneralInfoService {
	
	public Map<String, Object> getGeneralInfo(){
		Map<String, Object> map = new HashMap<String, Object>();
		for(GeneralInfoMonitor gIm : GeneralInfoMonitor.values()){
			map.put(gIm.getName(), gIm.getInfo());
		}
		return map;
	}
	
}

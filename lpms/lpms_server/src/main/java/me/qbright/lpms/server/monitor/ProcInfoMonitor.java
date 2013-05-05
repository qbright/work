package me.qbright.lpms.server.monitor;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import me.qbright.lpms.server.data.DataUtil;
import me.qbright.lpms.server.data.ProcInfoData;

/**
 * @author QBRIGHT
 * @date 2013-5-4
 */
public enum ProcInfoMonitor {
	PROCINFO_LIST("procinfo_list",List.class){
		public String getInfo(){
			try {
				return om.writeValueAsString(procInfoData.getProcInfos());
			} catch ( IOException e) {
				log.error("进程信息获取失败",e);
				return "{}";
			}
		}
	};
	
	private String name;
	private Class<?> returnType;
	
	private static ProcInfoData procInfoData = DataUtil.PROC_INFO_DATA;
	private static Logger log = Logger.getLogger(ProcInfoMonitor.class);
	private static ObjectMapper om = new ObjectMapper();
	private ProcInfoMonitor(String name,Class<?> returnType){
		this.name = name;
		this.returnType = returnType;
	}
	
	abstract public String getInfo();

	public String getName() {
		return name;
	}

	public Class<?> getReturnType() {
		return returnType;
	}
	
	
}

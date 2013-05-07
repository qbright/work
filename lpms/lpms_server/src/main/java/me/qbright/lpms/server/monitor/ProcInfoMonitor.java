package me.qbright.lpms.server.monitor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import me.qbright.lpms.server.data.DataUtil;
import me.qbright.lpms.server.data.ProcInfoData;
import me.qbright.lpms.server.datamodule.ProcInfoModule;

/**
 * @author QBRIGHT
 * @date 2013-5-4
 */
public enum ProcInfoMonitor {
	PROCINFO_LIST("procInfo_list",List.class){
		public String getInfo(){
			try {
				List<ProcInfoModule> list = procInfoData.getProcInfos();
				Collections.sort(list);
				return om.writeValueAsString(list);
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

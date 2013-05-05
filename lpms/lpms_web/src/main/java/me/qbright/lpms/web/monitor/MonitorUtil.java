package me.qbright.lpms.web.monitor;

import java.util.List;
import java.util.Map;

/**
 * @author QBRIGHT
 * @date 2013-5-5
 */
public enum MonitorUtil {
	GENERAL_INFO("/lpms_server/generalInfo",Map.class),
	FILESYSTEM_INFO("/lpms_server/fileSystemInfo",List.class);
	
	private String path;
	
	private Class<?> returnType;
	
	private MonitorUtil(String path,Class<?> returnType){
		this.path= path;
		this.returnType = returnType;
	}


	public String getPath() {
		return path;
	}

	public Class<?> getReturnType() {
		return returnType;
	}
	
	
}

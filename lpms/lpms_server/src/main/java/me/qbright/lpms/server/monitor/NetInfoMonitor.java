package me.qbright.lpms.server.monitor;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

import me.qbright.lpms.server.data.DataUtil;
import me.qbright.lpms.server.data.NetInfoData;

/**
 * @author QBRIGHT
 * @date 2013-4-30
 */
public enum NetInfoMonitor {
	NETINFO_LIST("netInfo_list",List.class){
		public String getInfo(){
			ObjectMapper om = new ObjectMapper();
			try {
				return om.writeValueAsString(netInfoData.getNetInfos());
			} catch (IOException e) {
				log.error("数据获取失败",e);
				return "{ }";
			}
			
		}
	};
	
	private String name;
	private Class<?> returnType;
	
	private NetInfoMonitor(String name,Class<?> returnType){
		this.name = name;
		this.returnType = returnType;
	}
	
	
	abstract public String getInfo();
	private static NetInfoData netInfoData = DataUtil.NET_INFO_DATA;
	private static Logger log = Logger.getLogger(NetInfoMonitor.class);
	public String getName(){
		return this.name;
	}
	public Class<?> getReturnType(){
		return returnType;
	}
	
	
}

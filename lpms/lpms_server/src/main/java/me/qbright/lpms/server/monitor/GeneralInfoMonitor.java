package me.qbright.lpms.server.monitor;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;

import me.qbright.lpms.server.data.DataUtil;
import me.qbright.lpms.server.data.GeneralInfoData;

/**
 * @author QBRIGHT
 * @date 2013-4-18
 */
public enum GeneralInfoMonitor {
	SYSTEM_HOSTNAME("system_hostname",String.class) {
		public String getInfo() {
			System.out.println(generalInfoData);
			return generalInfoData.systemHostName();
		}
	},
	OPERATING_SYSTEM("operating_system",String.class){
		@Override
		public String getInfo() {
			return generalInfoData.operationSystem();
		}
	},TIME_ON_SYSTEM("time_on_system",String.class){
		@Override
		public String getInfo() {
			return generalInfoData.timeOnSystem();
		}
		
	},KERNEL_AND_CPU("kernel_and_cpu",String.class){
		@Override
		public String getInfo() {
			return generalInfoData.kernelAndCpu();
		}
	},PROCESSOR_INFO("processor_info",String.class){
		@Override
		public String getInfo() {
			return generalInfoData.processorInfo();
		}
	},SYSTEM_UPTIME("system_uptime",String.class){
		@Override
		public String getInfo() {
			return generalInfoData.sytemUptime();
		}
		
	},RUNNING_PROCESSES("running_processes",String.class){
		@Override
		public String getInfo() {
			return generalInfoData.runningProvesses();
		}
	},CPU_USAGE("cpu_usagea",String.class){
		@Override
		public String getInfo() {
			return generalInfoData.cpuUsage();
		}
	},REAL_MEMORY("real_memory",Map.class){
		@Override
		public String getInfo() {
			Map<String, String> map = new HashMap<String, String>();
			map.put("total", generalInfoData.realMemoryTotal());
			map.put("used", generalInfoData.realMemoryUsed());
			try {
				return om.writeValueAsString(map);
			} catch (IOException e) {
				e.printStackTrace();
				return "{\"total \" : \"0 KB \",\"used \" : \"0 KB \"}";
			}
		}
		
	},SWAP_MEMORY("swap_memory",Map.class){
		@Override
		public String getInfo() {
			Map<String, String> map = new HashMap<String, String>();
			map.put("total", generalInfoData.swapMemoryTotal());
			map.put("used", generalInfoData.swapMemoryUsed());
			try {
				return om.writeValueAsString(map);
			} catch (IOException e) {
				e.printStackTrace();
				return "{\"total \" : \"0 KB \",\"used \" : \"0 KB \"}";
			}
		}
		
	},LOCAL_DISK("local_disk",Map.class){
		@Override
		public String getInfo() {
			Map<String, String> map = new HashMap<String, String>();
			map.put("total", generalInfoData.localDiskTotal());
			map.put("used", generalInfoData.localDiskUsed());
			try {
				return om.writeValueAsString(map);
			} catch (IOException e) {
				e.printStackTrace();
				return "{\"total \" : \"0 KB \",\"used \" : \"0 KB \"}";
			}
		}
		
	};
	
	
	
	private static GeneralInfoData generalInfoData = DataUtil.GENERAL_INFO_DATA;
	private String name;
	private Class<?> returnType;

	private static ObjectMapper om = new ObjectMapper();
	
	private GeneralInfoMonitor(String name,Class<?> returnType) {
		this.name = name;
		this.returnType = returnType;
	}
	
	
	public abstract String getInfo();

	public Class<?> getReturnType(){
		return this.returnType;
	}
	public String getName() {
		return this.name;
	}
}

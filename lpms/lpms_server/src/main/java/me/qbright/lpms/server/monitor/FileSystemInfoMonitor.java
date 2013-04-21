/**
 * @author qbright
 * 2013-4-24
 */
package me.qbright.lpms.server.monitor;

import java.io.IOException;
import java.util.List;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import me.qbright.lpms.server.data.DataUtil;
import me.qbright.lpms.server.data.FileSystemInfoData;

/**
 * @author QBRIGHT
 * @date 2013-4-24
 */
public enum FileSystemInfoMonitor {
	FILESYSTEM_LIST("fileSystem_list",List.class){
		@Override
		public String getInfo(){
			ObjectMapper om = new ObjectMapper();
			try {
				return om.writeValueAsString(fileSystemInfoData.getFileSystemList());
			} catch (IOException e) {
				e.printStackTrace();
				return "{ }";
			}
		}
	};
	
	
	
	private static FileSystemInfoData fileSystemInfoData = DataUtil.FILESYSTEM_INFO_DATA;
	private String name;
	private Class<?> returnType;
	
	private FileSystemInfoMonitor(String name,Class<?> returnType){
		this.name = name;
		this.returnType = returnType;
	}
	
	public String getName(){
		return this.name;
	}
	public Class<?> getReturnType(){
		return this.returnType;
	}
	
	public abstract String getInfo();
}

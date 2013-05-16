package me.qbright.lpms.server.data;

import java.util.List;

import me.qbright.lpms.server.datamodule.FileSystemModule;


/**
 * @author QBRIGHT
 * @date 2013-4-23
 */
public interface FileSystemInfoData {
	/**
	 * 获取文件系统列表
	 * @return
	 */
	List<FileSystemModule> getFileSystemList();
	
	
}

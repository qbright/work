package me.qbright.lpms.server.dataimpl;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import me.qbright.lpms.server.data.DataUtil;
import me.qbright.lpms.server.data.FileSystemInfoData;
import me.qbright.lpms.server.data.DataUtil.UNIT;
import me.qbright.lpms.server.datamodule.FileSystemModule;

import org.apache.log4j.Logger;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * @author QBRIGHT
 * @date 2013-4-24
 */
public class FileSystemInfoDataImpl implements FileSystemInfoData {
	private static Sigar sigar = SingletonSigar.SIGAE.getInstance();
	private Logger log = Logger.getLogger(FileSystemInfoDataImpl.class);
	private List<FileSystem> fileSystemList;

	@Override
	public List<FileSystemModule> getFileSystemList() {
		if (getLocalFileSystems() == null) {
			return null;
		}
		List<FileSystemModule> fileSystemModuleList = new ArrayList<FileSystemModule>();
		for (FileSystem fs : fileSystemList) {
			fileSystemModuleList.add(getModule(fs));
		}
		return fileSystemModuleList;
	}

	private FileSystemModule getModule(FileSystem fs) {
		FileSystemModule fsm = new FileSystemModule();
		fsm.setDevName(fs.getDevName());
		fsm.setDirName(fs.getDirName());
		try {
			fsm.setDiskReads(String.valueOf(sigar.getFileSystemUsage(
					fs.getDirName()).getDiskReadBytes()));
			fsm.setDiskWrites(String.valueOf(sigar.getFileSystemUsage(
					fs.getDirName()).getDiskWriteBytes()));
			fsm.setFree(DataUtil.changeCapacity(
					sigar.getFileSystemUsage(fs.getDirName()).getFree() * 1024,
					UNIT.AUTO));
			fsm.setSysTypeName(fs.getSysTypeName());
			fsm.setTotal(DataUtil.changeCapacity(
					sigar.getFileSystemUsage(fs.getDirName()).getTotal() * 1024,
					UNIT.AUTO));
			fsm.setUsed(DataUtil.changeCapacity(
					sigar.getFileSystemUsage(fs.getDirName()).getUsed() * 1024,
					UNIT.AUTO));
			fsm.setTypeName(fs.getTypeName());
			return fsm;
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			log.error("数据获取失败", e);
			return new FileSystemModule();
		}
	}

	private List<FileSystem> getLocalFileSystems() {
		try {
			fileSystemList = Arrays.asList(sigar.getFileSystemList());
			fileSystemList = new ArrayList<FileSystem>(fileSystemList);
			for (int i = 0; i < fileSystemList.size(); i++) {
				if (fileSystemList.get(i).getType() != FileSystem.TYPE_LOCAL_DISK) {
					fileSystemList.remove(i);
				}
			}
			return fileSystemList;
		} catch (SigarException e) {
			// TODO Auto-generated catch block
			log.error("数据转换失败", e);
			return new ArrayList<FileSystem>();
		}

	}

}

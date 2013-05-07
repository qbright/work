package me.qbright.lpms.server.datamodule;

/**
 * @author QBRIGHT
 * @date 2013-4-24
 */
public class FileSystemModule {
	private String devName;
	private String dirName;
	private String sysTypeName;
	private String typeName;
	private String total;
	private String free;
	private String used;
	private String diskReads;
	private String diskWrites;
	private String usedPercent;
	private String freePercent;

	public String getDevName() {
		return devName;
	}

	
	public void setDevName(String devName) {
		this.devName = devName;
	}

	
	public String getDirName() {
		return dirName;
	}

	
	public void setDirName(String dirName) {
		this.dirName = dirName;
	}

	
	public String getSysTypeName() {
		return sysTypeName;
	}

	
	public void setSysTypeName(String sysTypeName) {
		this.sysTypeName = sysTypeName;
	}

	
	public String getTypeName() {
		return typeName;
	}

	
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	
	public String getTotal() {
		return total;
	}

	
	public void setTotal(String total) {
		this.total = total;
	}

	public String getFree() {
		return free;
	}

	
	public void setFree(String free) {
		this.free = free;
	}

	public String getUsed() {
		return used;
	}

	
	public void setUsed(String used) {
		this.used = used;
	}

	
	public String getDiskReads() {
		return diskReads;
	}

	
	public void setDiskReads(String diskReads) {
		this.diskReads = diskReads;
	}

	
	public String getDiskWrites() {
		return diskWrites;
	}

	
	public void setDiskWrites(String diskWrites) {
		this.diskWrites = diskWrites;
	}


	public String getUsedPercent() {
		return usedPercent;
	}


	public void setUsedPercent(String usedPercent) {
		this.usedPercent = usedPercent;
	}



	public String getFreePercent() {
		return freePercent;
	}

	public void setFreePercent(String freePercent) {
		this.freePercent = freePercent;
	}
	
	
}

/**
 * @author qbright
 * 2013-4-24
 */
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

	/**
	 * 获取文件系统名称
	 * @return the devName
	 */
	public String getDevName() {
		return devName;
	}

	/**
	 * @param devName
	 *            the devName to set
	 */
	public void setDevName(String devName) {
		this.devName = devName;
	}

	/**
	 * 获取文件系统路径
	 * @return the dirName
	 */
	public String getDirName() {
		return dirName;
	}

	/**
	 * @param dirName
	 *            the dirName to set
	 */
	public void setDirName(String dirName) {
		this.dirName = dirName;
	}

	/**
	 * 文件系统类型
	 * @return the sysTypeName
	 */
	public String getSysTypeName() {
		return sysTypeName;
	}

	/**
	 * @param sysTypeName
	 *            the sysTypeName to set
	 */
	public void setSysTypeName(String sysTypeName) {
		this.sysTypeName = sysTypeName;
	}

	/**
	 * 文件系统类型
	 * @return the typeName
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * @param typeName
	 *            the typeName to set
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	/**
	 * 文件系统总大小
	 * @return the total
	 */
	public String getTotal() {
		return total;
	}

	/**
	 * @param total
	 *            the total to set
	 */
	public void setTotal(String total) {
		this.total = total;
	}

	/**
	 * 文件系统空闲空间
	 * @return the free
	 */
	public String getFree() {
		return free;
	}

	/**
	 * @param free
	 *            the free to set
	 */
	public void setFree(String free) {
		this.free = free;
	}

	/**
	 * 文件系统已使用量
	 * @return the used
	 */
	public String getUsed() {
		return used;
	}

	/**
	 * @param used
	 *            the used to set
	 */
	public void setUsed(String used) {
		this.used = used;
	}

	/**
	 * 文件系统读取量
	 * @return the diskReads
	 */
	public String getDiskReads() {
		return diskReads;
	}

	/**
	 * @param diskReads
	 *            the diskReads to set
	 */
	public void setDiskReads(String diskReads) {
		this.diskReads = diskReads;
	}

	/**
	 * 文件系统写入量
	 * @return the diskWrites
	 */
	public String getDiskWrites() {
		return diskWrites;
	}

	/**
	 * @param diskWrites
	 *            the diskWrites to set
	 */
	public void setDiskWrites(String diskWrites) {
		this.diskWrites = diskWrites;
	}

}

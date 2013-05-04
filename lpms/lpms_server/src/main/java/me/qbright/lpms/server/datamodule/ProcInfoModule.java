package me.qbright.lpms.server.datamodule;

/**
 * @author QBRIGHT
 * @date 2013-5-4
 */
public class ProcInfoModule {
	private String pid;
	
	private String cpuPercent;

	private String cpuTime;

	private String user;

	private String procExe;

	private String memShare;

	private String memResident;

	private String memVirt;// getSize();

	private String procState;

	private String procNice;

	private String procPriority;

	/**
	 * @return the cpuPercent
	 */
	public String getCpuPercent() {
		return cpuPercent;
	}

	/**
	 * @param cpuPercent
	 *            the cpuPercent to set
	 */
	public void setCpuPercent(String cpuPercent) {
		this.cpuPercent = cpuPercent;
	}

	/**
	 * @return the cpuTime
	 */
	public String getCpuTime() {
		return cpuTime;
	}

	/**
	 * @param cpuTime
	 *            the cpuTime to set
	 */
	public void setCpuTime(String cpuTime) {
		this.cpuTime = cpuTime;
	}

	/**
	 * @return the user
	 */
	public String getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public void setUser(String user) {
		this.user = user;
	}

	/**
	 * @return the procExe
	 */
	public String getProcExe() {
		return procExe;
	}

	/**
	 * @param procExe
	 *            the procExe to set
	 */
	public void setProcExe(String procExe) {
		this.procExe = procExe;
	}

	/**
	 * @return the memShare
	 */
	public String getMemShare() {
		return memShare;
	}

	/**
	 * @param memShare
	 *            the memShare to set
	 */
	public void setMemShare(String memShare) {
		this.memShare = memShare;
	}

	/**
	 * @return the memResident
	 */
	public String getMemResident() {
		return memResident;
	}

	/**
	 * @param memResident
	 *            the memResident to set
	 */
	public void setMemResident(String memResident) {
		this.memResident = memResident;
	}

	/**
	 * @return the memVirt
	 */
	public String getMemVirt() {
		return memVirt;
	}

	/**
	 * @param memVirt
	 *            the memVirt to set
	 */
	public void setMemVirt(String memVirt) {
		this.memVirt = memVirt;
	}

	/**
	 * @return the procState
	 */
	public String getProcState() {
		return procState;
	}

	/**
	 * @param procState
	 *            the procState to set
	 */
	public void setProcState(String procState) {
		this.procState = procState;
	}

	/**
	 * @return the procNice
	 */
	public String getProcNice() {
		return procNice;
	}

	/**
	 * @param procNice
	 *            the procNice to set
	 */
	public void setProcNice(String procNice) {
		this.procNice = procNice;
	}

	/**
	 * @return the procPriority
	 */
	public String getProcPriority() {
		return procPriority;
	}

	/**
	 * @param procPriority
	 *            the procPriority to set
	 */
	public void setProcPriority(String procPriority) {
		this.procPriority = procPriority;
	}

	/**
	 * @return the pid
	 */
	public String getPid() {
		return pid;
	}

	/**
	 * @param pid the pid to set
	 */
	public void setPid(String pid) {
		this.pid = pid;
	}

}

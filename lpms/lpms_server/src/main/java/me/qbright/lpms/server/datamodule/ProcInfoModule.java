package me.qbright.lpms.server.datamodule;

/**
 * @author QBRIGHT
 * @date 2013-5-4
 */
public class ProcInfoModule implements Comparable<ProcInfoModule>{
	private String pid;
	
	private String procName;
	
	private String cpuPercent;

	private String cpuTime;

	private String user;

	private String procExe;

	private String memShare;

	private String memResident;

	private String memVirt;

	private String procState;

	private String procNice;

	private String procPriority;


	public String getCpuPercent() {
		return cpuPercent;
	}


	public void setCpuPercent(String cpuPercent) {
		this.cpuPercent = cpuPercent;
	}

	
	public String getCpuTime() {
		return cpuTime;
	}


	public void setCpuTime(String cpuTime) {
		this.cpuTime = cpuTime;
	}

	
	public String getUser() {
		return user;
	}


	public void setUser(String user) {
		this.user = user;
	}


	public String getProcExe() {
		return procExe;
	}


	public void setProcExe(String procExe) {
		this.procExe = procExe;
	}


	public String getMemShare() {
		return memShare;
	}


	public void setMemShare(String memShare) {
		this.memShare = memShare;
	}


	public String getMemResident() {
		return memResident;
	}


	public void setMemResident(String memResident) {
		this.memResident = memResident;
	}


	public String getMemVirt() {
		return memVirt;
	}


	public void setMemVirt(String memVirt) {
		this.memVirt = memVirt;
	}


	public String getProcState() {
		return procState;
	}


	public void setProcState(String procState) {
		this.procState = procState;
	}


	public String getProcNice() {
		return procNice;
	}


	public void setProcNice(String procNice) {
		this.procNice = procNice;
	}


	public String getProcPriority() {
		return procPriority;
	}


	public void setProcPriority(String procPriority) {
		this.procPriority = procPriority;
	}


	public String getPid() {
		return pid;
	}


	public void setPid(String pid) {
		this.pid = pid;
	}
	
	
	public String getProcName() {
		return procName;
	}


	public void setProcName(String procName) {
		this.procName = procName;
	}


	@Override
	public int compareTo(ProcInfoModule procInfoModule) {
		return -cpuPercent.compareTo(procInfoModule.getCpuPercent());
	}

}

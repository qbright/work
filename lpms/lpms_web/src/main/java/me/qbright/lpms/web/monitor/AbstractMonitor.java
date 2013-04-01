/**
 * @author qbright
 *
 * @date 2013-2-7
 */
package me.qbright.lpms.web.monitor;

public abstract class AbstractMonitor {
	private MonitorType monitorType;
	private String mechineIp;
	private int port;
	private String mechineName;
	private String mechinePassword;

	public MonitorType getMonitorType() {
		return monitorType;
	}

	public void setMonitorType(MonitorType monitorType) {
		this.monitorType = monitorType;
	}

	public String getMechineIp() {
		return mechineIp;
	}

	public void setMechineIp(String mechineIp) {
		this.mechineIp = mechineIp;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getMechineName() {
		return mechineName;
	}

	public void setMechineName(String mechineName) {
		this.mechineName = mechineName;
	}

	public String getMechinePassword() {
		return mechinePassword;
	}

	public void setMechinePassword(String mechinePassword) {
		this.mechinePassword = mechinePassword;
	}
}

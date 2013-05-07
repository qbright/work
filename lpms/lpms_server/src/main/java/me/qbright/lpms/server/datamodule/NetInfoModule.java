package me.qbright.lpms.server.datamodule;

/**
 * @author QBRIGHT
 * @date 2013-4-30
 */
public class NetInfoModule {
	private String description;

	private String macAdress;

	/**
	 * mtu String 通信术语 最大传输单元（Maximum Transmission Unit，MTU）是指一种通信协议的某一层上面所能通
	 * 过的最大数据包大小（以字节为单位）。最大传输单元这个参数通常与通信接口有关（网络接口卡、串口等）。
	 */
	private String mtu;

	private String type;

	private String ipAddress;

	private String netMask;

	private String rxPackets;

	private String txPackets;

	private String rxBytes;

	private String txBytes;

	private String rxErrors;

	private String txErrors;

	private String rxDropped;

	private String txDropped;

	@SuppressWarnings("unused")
	private Boolean isNotPackets;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getMacAdress() {
		return macAdress;
	}

	public void setMacAdress(String macAdress) {
		this.macAdress = macAdress;
	}

	public String getMtu() {
		return mtu;
	}

	public void setMtu(String mtu) {
		this.mtu = mtu;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	public String getRxPackets() {
		return rxPackets;
	}

	public void setRxPackets(String rxPackets) {
		this.rxPackets = rxPackets;
	}

	public String getTxPackets() {
		return txPackets;
	}

	public void setTxPackets(String txPackets) {
		this.txPackets = txPackets;
	}

	public String getRxBytes() {
		return rxBytes;
	}

	public void setRxBytes(String rxBytes) {
		this.rxBytes = rxBytes;
	}

	public String getTxBytes() {
		return txBytes;
	}

	public void setTxBytes(String txBytes) {
		this.txBytes = txBytes;
	}

	public String getRxErrors() {
		return rxErrors;
	}

	public void setRxErrors(String rxErrors) {
		this.rxErrors = rxErrors;
	}

	public String getTxErrors() {
		return txErrors;
	}

	public void setTxErrors(String txErrors) {
		this.txErrors = txErrors;
	}

	public String getRxDropped() {
		return rxDropped;
	}

	public void setRxDropped(String rxDropped) {
		this.rxDropped = rxDropped;
	}

	public String getTxDropped() {
		return txDropped;
	}

	public void setTxDropped(String txDropped) {
		this.txDropped = txDropped;
	}

	public Boolean getIsNotPackets() {
		return ((getRxPackets().equals("0") || getRxDropped() == null)
				&& (getRxDropped().equals("0") || getRxDropped() == null)
				&& (getRxBytes().equals("0") || getRxBytes() == null)
				&& (getTxBytes().equals("0") || getTxBytes() == null)
				&& (getTxDropped().equals("0") || getTxBytes() == null) && (getTxPackets()
				.equals("0") || getTxPackets() == null));

	}

	public String getNetMask() {
		return netMask;
	}

	public void setNetMask(String netMask) {
		this.netMask = netMask;
	}

}

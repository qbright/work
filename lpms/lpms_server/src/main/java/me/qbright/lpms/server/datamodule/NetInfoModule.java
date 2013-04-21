package me.qbright.lpms.server.datamodule;

/**
 * @author QBRIGHT
 * @date 2013-4-30
 */
public class NetInfoModule {
	private String description;

	private String macAdree;

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

	private Boolean isNotPackets;

	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description
	 *            the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @return the macAdree
	 */
	public String getMacAdree() {
		return macAdree;
	}

	/**
	 * @param macAdree
	 *            the macAdree to set
	 */
	public void setMacAdree(String macAdree) {
		this.macAdree = macAdree;
	}

	/**
	 * @return the mtu
	 */
	public String getMtu() {
		return mtu;
	}

	/**
	 * @param mtu
	 *            the mtu to set
	 */
	public void setMtu(String mtu) {
		this.mtu = mtu;
	}

	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * @param type
	 *            the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * @return the ipAddress
	 */
	public String getIpAddress() {
		return ipAddress;
	}

	/**
	 * @param ipAddress
	 *            the ipAddress to set
	 */
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}


	/**
	 * @return the rxPackets
	 */
	public String getRxPackets() {
		return rxPackets;
	}

	/**
	 * @param rxPackets
	 *            the rxPackets to set
	 */
	public void setRxPackets(String rxPackets) {
		this.rxPackets = rxPackets;
	}

	/**
	 * @return the txPackets
	 */
	public String getTxPackets() {
		return txPackets;
	}

	/**
	 * @param txPackets
	 *            the txPackets to set
	 */
	public void setTxPackets(String txPackets) {
		this.txPackets = txPackets;
	}

	/**
	 * @return the rxBytes
	 */
	public String getRxBytes() {
		return rxBytes;
	}

	/**
	 * @param rxBytes
	 *            the rxBytes to set
	 */
	public void setRxBytes(String rxBytes) {
		this.rxBytes = rxBytes;
	}

	/**
	 * @return the txBytes
	 */
	public String getTxBytes() {
		return txBytes;
	}

	/**
	 * @param txBytes
	 *            the txBytes to set
	 */
	public void setTxBytes(String txBytes) {
		this.txBytes = txBytes;
	}

	/**
	 * @return the rxErrors
	 */
	public String getRxErrors() {
		return rxErrors;
	}

	/**
	 * @param rxErrors
	 *            the rxErrors to set
	 */
	public void setRxErrors(String rxErrors) {
		this.rxErrors = rxErrors;
	}

	/**
	 * @return the txErrors
	 */
	public String getTxErrors() {
		return txErrors;
	}

	/**
	 * @param txErrors
	 *            the txErrors to set
	 */
	public void setTxErrors(String txErrors) {
		this.txErrors = txErrors;
	}

	/**
	 * @return the rxDropped
	 */
	public String getRxDropped() {
		return rxDropped;
	}

	/**
	 * @param rxDropped
	 *            the rxDropped to set
	 */
	public void setRxDropped(String rxDropped) {
		this.rxDropped = rxDropped;
	}

	/**
	 * @return the txDropped
	 */
	public String getTxDropped() {
		return txDropped;
	}

	/**
	 * @param txDropped
	 *            the txDropped to set
	 */
	public void setTxDropped(String txDropped) {
		this.txDropped = txDropped;
	}

	/**
	 * @return the isNotPackets
	 */
	public Boolean getIsNotPackets() {
		return ((getRxPackets().equals("0") || getRxDropped() == null) && (getRxDropped()
				.equals("0") || getRxDropped() == null));

	}

	/**
	 * @return the netMask
	 */
	public String getNetMask() {
		return netMask;
	}

	/**
	 * @param netMask the netMask to set
	 */
	public void setNetMask(String netMask) {
		this.netMask = netMask;
	}
	
}

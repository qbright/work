package me.qbright.lpms.server.data;

/**
 * @author QBRIGHT
 * @date 2013-4-22
 */
public interface JavaEnvironmentData {
	/**
	 * 获取虚拟机可使用的总内存
	 * @return
	 */
	public String totalMemory();
	
	/**
	 * 获取虚拟机可使用的剩余内存
	 * @return
	 */
	public String freeMemory();
	
	/**
	 * 虚拟机可使用的处理器数量
	 * @return
	 */
	public String availableProcessor();
	
	/**
	 * java运行环境版本
	 * @return
	 */
	public String jdkVersion(); 
	
	/**
	 * java安装路径
	 * @return
	 */
	public String javaHome();
	
	/**
	 * java虚拟机名称
	 * @return
	 */
	public String javaVmName();
	/**
	 * java虚拟机版本
	 * @return
	 */
	public String javaVmVersion();
	
	/**
	 * java内存使用百分比
	 * @return
	 */
	public String memoryUsedPercent();
}

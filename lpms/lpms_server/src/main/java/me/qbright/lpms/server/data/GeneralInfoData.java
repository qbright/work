package me.qbright.lpms.server.data;

/**
 * @author QBRIGHT
 * @date 2013-4-18
 */
public interface GeneralInfoData {
	/**
	 * 返回计算机域名及ip地址
	 * 
	 * @return
	 */
	String systemHostName();

	/**
	 * 获取操作系统信息
	 * 
	 * @return
	 */
	String operationSystem();

	/**
	 * 系统时间
	 * 
	 * @return
	 */
	String timeOnSystem();

	/**
	 * 内核及Cpu信息
	 * 
	 * @return
	 */
	String kernelAndCpu();

	/**
	 * cpu信息
	 * 
	 * @return
	 */
	String processorInfo();

	/**
	 * 系统启动时间
	 * 
	 * @return
	 */
	String sytemUptime();

	/**
	 * 运行进程数量
	 * 
	 * @return
	 */
	String runningProvesses();

	/**
	 * cpu使用情况
	 * 各个计算公式：<br>
	 * CPUTime = user + system + nice + idle + iowait + irq + softirq +stl <br>
	 * %us = (userTime + niceTime )/CPUTime *100% <br>
	 * %sy = (systemTime + hardIrqTime + softIroTime )/CPUTime *100% <br>
	 * %idle = (idleTime)/CPUTime *100% <br>
	 * %wait = (waitTime)/CPUTime *100% <br>
	 * %hi = (hardIrqTime)/CPUTime *100% <br>
	 * %si = (softIroTime)/CPUTime *100% <br>
	 * $st = (stealTime)/CPUTime *100% <br>
	 * @return
	 */
	String cpuUsage();
	/**
	 * 物理内存总量
	 * 
	 * @return
	 */
	String realMemoryTotal();

	/**
	 * 物理内存使用量
	 * 
	 * @return
	 */
	String realMemoryUsed();

	/**
	 * 交换区内存总量
	 * 
	 * @return
	 */
	String swapMemoryTotal();

	/**
	 * 交换区内存使用量
	 * 
	 * @return
	 */
	String swapMemoryUsed();

	/**
	 * 本地硬盘总量
	 * 
	 * @return
	 */
	String localDiskTotal();

	/**
	 * 本地硬盘使用量
	 * 
	 * @return
	 */
	String localDiskUsed();
	
	
	/**
	 * 本地硬盘使用百分比
	 * @return
	 */
	String localDiskUsedPrecent();
	
	
	/**
	 * 交换区内存使用百分比
	 * @return
	 */
	String swapMemoryUsedPrecent();
	
	
	/**
	 * 物理内存使用百分比
	 * @return
	 */
	String realMemoryUsedPrecent();
}

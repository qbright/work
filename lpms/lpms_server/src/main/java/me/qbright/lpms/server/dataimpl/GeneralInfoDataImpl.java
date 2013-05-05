package me.qbright.lpms.server.dataimpl;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hyperic.sigar.Cpu;
import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.FileSystem;
import org.hyperic.sigar.FileSystemUsage;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.OperatingSystem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.hyperic.sigar.Swap;

import me.qbright.lpms.server.data.DataUtil;
import me.qbright.lpms.server.data.DataUtil.UNIT;
import me.qbright.lpms.server.data.GeneralInfoData;

/**
 * @author QBRIGHT
 * @date 2013-4-18
 */
public class GeneralInfoDataImpl implements GeneralInfoData {

	private static Logger log = Logger.getLogger(GeneralInfoDataImpl.class);
	private static Sigar sigar = SingletonSigar.SIGAE.getInstance();
	private static Properties proc = System.getProperties();
	private static OperatingSystem os = OperatingSystem.getInstance();

	@Override
	public String systemHostName() {
		try {

			StringBuffer sb = new StringBuffer(InetAddress.getLocalHost()
					.getHostName()).append(" (")
					.append(InetAddress.getLocalHost().getHostAddress())
					.append(")");
			return sb.toString();
		} catch (UnknownHostException e) {
			log.error("获取IP地址失败", e);
			return "unknow host (0.0.0.0)";
		}

	}

	@Override
	public String operationSystem() {
		StringBuffer bs = new StringBuffer(os.getDescription()).append(" ")
				.append(os.getArch()).append(" ")
				.append(proc.getProperty("os.version"));
		return bs.toString();
	}

	@Override
	public String timeOnSystem() {
		return new Date().toString();
	}

	@Override
	public String kernelAndCpu() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String processorInfo() {
		CpuInfo[] cpuInfo;
		try {
			cpuInfo = sigar.getCpuInfoList();
			StringBuffer bs = new StringBuffer(cpuInfo[0].getVendor())
					.append(" ").append(cpuInfo[0].getModel()).append(" ")
					.append(cpuInfo.length).append("cores");
			return bs.toString();
		} catch (SigarException e) {
			log.error("cpu信息获取失败", e);
			return "unknow cpu info";
		}
	}

	@Override
	public String sytemUptime() {
		try {
			double uptime = sigar.getUptime().getUptime();
			return getUptime(new Double(uptime).longValue());

		} catch (SigarException e) {
			log.error("获取系统时间失败", e);
			return " unknow uptime";
		}

	}

	@Override
	public String runningProvesses() {
		try {
			return String.valueOf(sigar.getProcList().length);
		} catch (SigarException e) {
			log.error("获取进程数失败", e);
			return "0";
		}
	}

	@Override
	public String realMemoryTotal() {
		Mem mem;
		try {
			mem = sigar.getMem();
			return DataUtil.changeCapacity(mem.getTotal(), UNIT.AUTO);
		} catch (SigarException e) {
			log.error("内存容量获取失败", e);
			return "0 KB";
		}
	}

	@Override
	public String realMemoryUsed() {
		Mem mem;
		try {
			mem = sigar.getMem();
			return DataUtil.changeCapacity(mem.getUsed(), UNIT.AUTO);
		} catch (SigarException e) {
			log.error("内存获取失败", e);
			return "0 KB";
		}
	}

	@Override
	public String swapMemoryTotal() {
		Swap swap;
		try {
			swap = sigar.getSwap();
			return DataUtil.changeCapacity(swap.getTotal(), UNIT.AUTO);
		} catch (SigarException e) {
			log.error("交换区内存获取失败", e);
			return "0 KB";
		}
	}

	@Override
	public String swapMemoryUsed() {
		Swap swap;
		try {
			swap = sigar.getSwap();
			return DataUtil.changeCapacity(swap.getUsed(), UNIT.AUTO);
		} catch (SigarException e) {
			log.error("交换区内存获取失败", e);
			return "0 KB";
		}
	}

	@Override
	public String localDiskTotal() {
		long total = 0;
		try {
			total = getLocalDiskTotal(sigar.getFileSystemList());
			return DataUtil.changeCapacity(total, UNIT.AUTO);
		} catch (SigarException e) {
			log.error("获取硬盘数据失败", e);
			return "0 KB";
		}
	}

	@Override
	public String localDiskUsed() {
		long used = 0;
		try {
			 used = getLocalDiskUsed(sigar.getFileSystemList());
			return DataUtil.changeCapacity(used, UNIT.AUTO);
		} catch (SigarException e) {
			log.error("获取硬盘数据失败", e);
			return "0 KB";
		}
	}
	
	private Long getLocalDiskUsed(FileSystem[] fs) throws SigarException{
		long used = 0;
		for (FileSystem fs_ : fs) {
			if (fs_.getTypeName().equals("local")) {
				FileSystemUsage fsu = sigar.getFileSystemUsage(fs_
						.getDirName());
				used += (fsu.getUsed() * 1024);
			}
		}
		return used;
	}
	
	private Long getLocalDiskTotal(FileSystem[] fs) throws SigarException{
		long total = 0;
		for (FileSystem fs_ : fs) {
			if (fs_.getTypeName().equals("local")) {
				FileSystemUsage fsu = sigar.getFileSystemUsage(fs_
						.getDirName());
				total += (fsu.getTotal() * 1024);
			}
		}
		return total;
	}
	
	@Override
	public String cpuUsage() {
		Cpu cpu;
		try {
			cpu = sigar.getCpu();
			long cpuTime = cpu.getUser() + cpu.getSys() + cpu.getNice()
					+ cpu.getIdle() + cpu.getWait() + cpu.getIrq()
					+ cpu.getSoftIrq() + cpu.getStolen();
			StringBuffer sb = new StringBuffer();
			String userpre = DataUtil.getPercent(
					(cpu.getUser() + cpu.getNice()), cpuTime, 2);
			String syspre = DataUtil.getPercent((cpu.getSys() + cpu.getIrq()),
					cpuTime, 2);
			String waitpre = DataUtil.getPercent(cpu.getWait(), cpuTime, 2);
			String idlepre = DataUtil.getPercent(cpu.getIdle(), cpuTime, 2);
			sb.append(userpre).append(" user, ").append(syspre)
					.append(" system, ").append(waitpre).append(" I/O wait, ")
					.append(idlepre).append(" idle");
			return sb.toString();
		} catch (SigarException e) {
			log.error("获取cpu使用情况失败", e);
			return "0% user, 0% system, 0% I/O wait, 0% idle";
		}
	}

	public static String getUptime(long uptime) {
		StringBuffer sb = new StringBuffer();
		sb.append(uptime / 86400).append(" d ")
				.append((uptime % 86400) / 3600).append(" h ")
				.append(((uptime % 86400) % 3600) / 60).append(" m ")
				.append((((uptime % 86400) % 3600) % 60)).append(" s");
		return sb.toString();
	}

	@Override
	public String localDiskUsedPrecent() {
		FileSystem[] fs;
		try {
			fs = sigar.getFileSystemList();
			long total = getLocalDiskTotal(fs);
			long used = getLocalDiskUsed(fs);
			return DataUtil.getPercent(used, total, 2);
		} catch (SigarException e) {
			log.error("获取硬盘使用百分比失败",e);
			return "0 %";
		}
	}

	@Override
	public String swapMemoryUsedPrecent() {
		try {
			Swap swap = sigar.getSwap();
			long total = swap.getTotal();
			long used = swap.getUsed();
			return DataUtil.getPercent(used, total, 2);
		} catch (SigarException e) {
			log.error("获取交换区使用百分比失败",e);
			return "0 %";
		}
	}

	@Override
	public String realMemoryUsedPrecent() {
		try {
			Mem mem = sigar.getMem();
			long total = mem.getTotal();
			long used = mem.getUsed();
			return DataUtil.getPercent(used, total, 2);
		} catch (SigarException e) {
			log.error("获取物理内存使用百分比失败",e);
			return "0 %";
		}
	}

}

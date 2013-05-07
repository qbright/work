package me.qbright.lpms.server.dataimpl;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hyperic.sigar.ProcCpu;
import org.hyperic.sigar.ProcMem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import me.qbright.lpms.server.data.DataUtil;
import me.qbright.lpms.server.data.ProcInfoData;
import me.qbright.lpms.server.data.DataUtil.UNIT;
import me.qbright.lpms.server.datamodule.ProcInfoModule;

/**
 * @author QBRIGHT
 * @date 2013-5-4
 */
public class ProcInfoDataImpl implements ProcInfoData {

	private static Sigar sigar = SingletonSigar.SIGAE.getInstance();

	private Logger log = Logger.getLogger(ProcInfoDataImpl.class);

	private long[] procArray;

	private ProcCpu procCpu;

	private ProcMem procMem;

	@Override
	public List<ProcInfoModule> getProcInfos() {
		List<ProcInfoModule> procList = new ArrayList<ProcInfoModule>();
		ProcInfoModule procInfoModule;
		getProcList();
		for (int i = 0; i <= procArray.length; i++) {
			try {
				procInfoModule = getprocInfoModule(procArray[i]);
			} catch (Exception e) {
				continue;
			}
			procList.add(procInfoModule);
		}

		return procList;
	}

	private ProcInfoModule getprocInfoModule(long pid) throws SigarException {
		ProcInfoModule procInfoModule = new ProcInfoModule();
		procCpu = sigar.getProcCpu(pid);
		procMem = sigar.getProcMem(pid);
		procInfoModule.setPid(String.valueOf(pid));
		procInfoModule.setCpuPercent(changeRound(procCpu.getPercent()));

		procInfoModule.setCpuTime(getCpuTime(procCpu.getStartTime(),
				procCpu.getLastTime()));

		procInfoModule.setMemResident(DataUtil.changeCapacity(
				procMem.getResident(), UNIT.AUTO));

		procInfoModule.setMemShare(DataUtil.changeCapacity(procMem.getShare(),
				UNIT.AUTO));

		procInfoModule.setMemVirt(DataUtil.changeCapacity(procMem.getSize(),
				UNIT.AUTO));

		procInfoModule.setProcExe(getProcExe(sigar.getProcExe(pid).getName()));

		procInfoModule.setProcNice(String.valueOf(sigar.getProcState(pid)
				.getNice()));

		procInfoModule.setProcPriority(String.valueOf(sigar.getProcState(pid)
				.getPriority()));

		procInfoModule.setProcState(String.valueOf(sigar.getProcState(pid)
				.getState()));

		procInfoModule.setUser(sigar.getProcCredName(pid).getUser());

		return procInfoModule;
	}

	private String getProcExe(String exe) {
		String separator = (String) System.getProperties()
				.get("file.separator");// 不同系统文件分隔符不同
		return exe.substring(exe.lastIndexOf(separator) + 1, exe.length());
	}

	private String getCpuTime(long startDate, long lastDate) {
		long mss = lastDate - startDate;
		long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
		long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
		long seconds = (mss % (1000 * 60)) / 1000;
		String DOT = ":";
		StringBuffer sb = new StringBuffer();
		sb.append(hours).append(DOT).append(minutes).append(DOT)
				.append(seconds);

		return sb.toString();
	}

	private void getProcList() {
		try {
			this.procArray = sigar.getProcList();
		} catch (SigarException e) {
			log.error("获取进程ID失败", e);
		}
	}

	private static String changeRound(double a) {
		NumberFormat doubleFormat = new DecimalFormat("0.000");
		return doubleFormat.format(a) + "%";
	}

}

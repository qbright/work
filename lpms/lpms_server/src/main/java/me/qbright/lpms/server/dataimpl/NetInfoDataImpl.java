package me.qbright.lpms.server.dataimpl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.hyperic.sigar.NetFlags;
import org.hyperic.sigar.NetInterfaceConfig;
import org.hyperic.sigar.NetInterfaceStat;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import me.qbright.lpms.server.data.NetInfoData;
import me.qbright.lpms.server.datamodule.NetInfoModule;

/**
 * @author QBRIGHT
 * @date 2013-4-30
 */
public class NetInfoDataImpl implements NetInfoData {
	private static Sigar sigar = SingletonSigar.SIGAE.getInstance();
	private Logger log = Logger.getLogger(NetInfoDataImpl.class);
	private List<NetInterfaceConfig> netInfoLists;
	public static final String ZERO = "0";
	@Override
	public List<NetInfoModule> getNetInfos() {
		List<NetInfoModule> netInfoModules = new ArrayList<NetInfoModule>();
		getNetInfoLists();
		for (NetInterfaceConfig netInterfaceConfig : netInfoLists) {
			if(netInterfaceConfig.getHwaddr() == NetFlags.NULL_HWADDR){
				continue;
			}
			netInfoModules.add(getModule(netInterfaceConfig));
		}

		return netInfoModules;
	}

	private NetInfoModule getModule(NetInterfaceConfig netInterfaceConfig) {
		NetInfoModule netInfoModule = new NetInfoModule();
		netInfoModule.setNetMask(netInterfaceConfig.getNetmask());
		netInfoModule.setDescription(netInterfaceConfig.getDescription());
		netInfoModule.setIpAddress(netInterfaceConfig.getAddress());
		netInfoModule.setMacAdree(netInterfaceConfig.getHwaddr());
		netInfoModule.setMtu(String.valueOf(netInterfaceConfig.getMtu()));
		netInfoModule.setType(netInterfaceConfig.getType());

		try {
			NetInterfaceStat netInterfaceStat = sigar
					.getNetInterfaceStat(netInterfaceConfig.getName());
			netInfoModule.setRxBytes(String.valueOf(netInterfaceStat.getRxBytes()));
			netInfoModule.setRxDropped(String.valueOf(netInterfaceStat.getRxDropped()));
			netInfoModule.setRxErrors(String.valueOf(netInterfaceStat.getRxErrors()));
			netInfoModule.setRxPackets(String.valueOf(netInterfaceStat.getRxPackets()));
			netInfoModule.setTxBytes(String.valueOf(netInterfaceStat.getTxBytes()));
			netInfoModule.setTxDropped(String.valueOf(netInterfaceStat.getTxDropped()));
			netInfoModule.setTxErrors(String.valueOf(netInterfaceStat.getTxErrors()));
			netInfoModule.setTxPackets(String.valueOf(netInterfaceStat.getTxPackets()));
			
		} catch (SigarException e) {
			log.error("获取网络信息出错",e);
			netInfoModule.setRxBytes(ZERO);
			netInfoModule.setRxDropped(ZERO);
			netInfoModule.setRxErrors(ZERO);
			netInfoModule.setRxPackets(ZERO);
			netInfoModule.setTxBytes(ZERO);
			netInfoModule.setTxDropped(ZERO);
			netInfoModule.setTxErrors(ZERO);
			netInfoModule.setTxPackets(ZERO);
		}

		return netInfoModule;
	}

	private void getNetInfoLists() {
		String[] ifNames;
		netInfoLists = new ArrayList<NetInterfaceConfig>();
		try {
			ifNames = sigar.getNetInterfaceList();
			for (String name : ifNames) {
				netInfoLists.add(sigar.getNetInterfaceConfig(name));
			}
		} catch (SigarException e) {
			log.error("获取网卡信息失败", e);
			netInfoLists = new ArrayList<NetInterfaceConfig>();
		}

	}

}

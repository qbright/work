/**
 * @author qbright
 * 2013-4-30
 */
package me.qbright.lpms.server.data;

import java.util.List;

import me.qbright.lpms.server.datamodule.NetInfoModule;

/**
 * @author QBRIGHT
 * @date 2013-4-30
 */
public interface NetInfoData {
	List<NetInfoModule> getNetInfos();
}

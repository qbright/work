/**
 * @author qbright
 * 2013-5-4
 */
package me.qbright.lpms.server.data;

import java.util.List;

import me.qbright.lpms.server.datamodule.ProcInfoModule;

/**
 * @author QBRIGHT
 * @date 2013-5-4
 */
public interface ProcInfoData {
	List<ProcInfoModule> getProcInfos();
}

/**
 * @author qbright
 * 2013-4-4
 */
package me.qbright.lpms.server;

import me.qbright.lpms.common.ConfigCommon;
import me.qbright.lpms.common.CopyFileUtil;
import me.qbright.lpms.server.rest.RestletServer;
import me.qbright.lpms.server.rest.RootRestlet;

/**
 * @author QBRIGHT
 * @date 2013-4-4
 * 程序入口
 */
public class Main {
	public static void main(String[] args) {

		CopyFileUtil.copeFileByJar("sigar-lib", "sigar-lib");
		System.setProperty("java.library.path", "sigar-lib");
		RestletServer restletServer = new RestletServer(
				ConfigCommon.getKeyString("SERVICE_ROOT"),
				ConfigCommon.getKeyInt("SERVICE_PORT"), new RootRestlet());

		restletServer.start();
	}
}

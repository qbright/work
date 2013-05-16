package me.qbright.lpms.server;

import org.apache.log4j.Logger;

import me.qbright.lpms.common.ConfigCommon;
import me.qbright.lpms.common.CopyFileUtil;
import me.qbright.lpms.common.EncodeCommon;
import me.qbright.lpms.server.rest.RestletServer;
import me.qbright.lpms.server.rest.RootRestlet;

/**
 * @author QBRIGHT
 * @date 2013-4-4 程序入口
 */
public class Main {
	private static Logger logger = Logger.getLogger(Main.class);

	public static void main(String[] args) {
		try {
			if (System.getProperty("machineName") == null
					|| System.getProperty("machinePassword") == null) {
				throw new Exception(
						"machineName and machinePassowrd are both requested");
			} else {
				System.setProperty("machinePassword", EncodeCommon
						.digester(System.getProperty("machinePassword")));
			}
		} catch (Exception e) {
			logger.error(e);
			System.exit(1);
		}
		System.setProperty("org.restlet.engine.loggerFacadeClass",
				"org.restlet.ext.slf4j.Slf4jLoggerFacade");

		CopyFileUtil.copeFileByJar("sigar-lib", "sigar-lib");// jar包环境
		System.setProperty("java.library.path", "sigar-lib");// dev环境
		RestletServer restletServer = new RestletServer(
				ConfigCommon.getKeyString("SERVICE_ROOT"),
				ConfigCommon.getKeyInt("SERVICE_PORT"), new RootRestlet());
		restletServer.start();

	}

	public static boolean initAuthorized() {

		return false;
	}

}

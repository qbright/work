package me.qbright.lpms.server;
import org.apache.log4j.Logger;
import org.hyperic.sigar.Sigar;
import org.restlet.Restlet;

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
	private static Logger log = Logger.getLogger(Main.class);
	public static void main(String[] args)  {
		//CopyFileUtil.copeFileByJar("sigar-lib", "sigar-lib");//jar包环境
		System.setProperty("java.library.path", "target/classes/sigar-lib");//dev环境
		
		System.setProperty("org.restlet.engine.loggerFacadeClass", "org.restlet.ext.slf4j.Slf4jLoggerFacade"); 
		
		RestletServer restletServer = new RestletServer(
				ConfigCommon.getKeyString("SERVICE_ROOT"),
				ConfigCommon.getKeyInt("SERVICE_PORT"), new RootRestlet());
		restletServer.start();
	
	}
}

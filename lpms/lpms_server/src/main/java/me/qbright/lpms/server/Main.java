package me.qbright.lpms.server;
import me.qbright.lpms.common.ConfigCommon;
import me.qbright.lpms.server.rest.RestletServer;
import me.qbright.lpms.server.rest.RootRestlet;

/**
 * @author QBRIGHT
 * @date 2013-4-4
 * 程序入口
 */
public class Main {
	public static void main(String[] args)  {
		//CopyFileUtil.copeFileByJar("sigar-lib", "sigar-lib");//jar包环境
		
		System.setProperty("java.library.path", "target/classes/sigar-lib");//dev环境
		RestletServer restletServer = new RestletServer(
				ConfigCommon.getKeyString("SERVICE_ROOT"),
				ConfigCommon.getKeyInt("SERVICE_PORT"), new RootRestlet());
		restletServer.start();
	
	}
}

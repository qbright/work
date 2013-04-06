/**
 * @author qbright
 * 2013-4-18
 */
package me.qbright.lpms.server.dataimpl;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.log4j.Logger;
import org.hyperic.sigar.Sigar;

import me.qbright.lpms.server.data.GeneralInfoData;

/**
 * @author QBRIGHT
 * @date 2013-4-18
 */
public class GeneralInfoDataImpl implements GeneralInfoData{
	
	private static Logger log = Logger.getLogger(GeneralInfoDataImpl.class);
	private static Sigar sigar = SingletonSigar.SIGAE.getInstance();
	
	@Override
	public String systemHostName() {
		// TODO Auto-generated method stub
		try {
			StringBuffer sb = new StringBuffer(System.getenv("USERDOMAIN"))
			.append(" (")
			.append(InetAddress.getLocalHost().getHostAddress())
			.append(")");
			return sb.toString();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			log.error("获取IP地址失败",e);
			return System.getenv("USERDOMAIN") + " (0.0.0.0)";
		}
		
	}

}

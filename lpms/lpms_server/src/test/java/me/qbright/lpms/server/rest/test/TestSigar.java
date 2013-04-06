/**
 * @author qbright
 * 2013-4-5
 */
package me.qbright.lpms.server.rest.test;

import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

/**
 * @author QBRIGHT
 * @date 2013-4-5
 */
public class TestSigar {
	public static void main(String[] args) throws SigarException {
		System.out.println("===========================" + System.getProperty("user.dir"));
		
		System.setProperty("java.library.path", "sigar-lib");
		Sigar sigar = new Sigar();
		sigar.getCpuInfoList();
		
	}
}

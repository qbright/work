package me.qbright.sigar.test;

import java.util.Observable;
import java.util.Observer;

import org.hyperic.sigar.CpuInfo;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;
import org.junit.Test;


public class SigarTest {
	@Test
	public void BasciTest() throws SigarException {
		System.out.println(System.getProperty("user.dir"));
		System.setProperty("java.library.path", "src/main/resources/sigar-lib/");
		Sigar sigar = new Sigar();
		CpuInfo[] cpuInfos =  sigar.getCpuInfoList();
	}

	
}

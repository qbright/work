package me.qbright.lpms.server.dataimpl;

import org.hyperic.sigar.Sigar;

/**
 * @author QBRIGHT
 * @date 2013-4-18
 * 获取sigar单例
 */
public enum SingletonSigar {
	SIGAE {
		public Sigar getInstance(){
			
			
			
			return sigar;
		}
	};
	private static Sigar sigar = new Sigar();
	private SingletonSigar(){
		
	}
	public abstract Sigar getInstance();
}

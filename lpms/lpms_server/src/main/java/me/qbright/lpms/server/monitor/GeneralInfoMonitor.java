/**
 * @author qbright
 * 2013-4-18
 */
package me.qbright.lpms.server.monitor;
import me.qbright.lpms.server.data.DataUtil;
import me.qbright.lpms.server.data.GeneralInfoData;

/**
 * @author QBRIGHT
 * @date 2013-4-18
 */
public enum GeneralInfoMonitor {
	SYSTEM_HOSTNAME("system_hostname") {
		public String getInfo() {
			System.out.println(generalInfoData);
			return generalInfoData.systemHostName();
		}
	},
	OPERATING_SYSTEM("operating_system"){
		@Override
		public String getInfo() {
			return null;
			
		}
		
	};
	
	private static GeneralInfoData generalInfoData = DataUtil.GENERAL_INFO_DATA;
	
	private String name;

	private GeneralInfoMonitor(String name) {
		this.name = name;
	}

	public abstract String getInfo();

	public String getName() {
		return this.name;
	}
}

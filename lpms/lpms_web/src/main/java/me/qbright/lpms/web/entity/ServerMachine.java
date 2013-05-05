/**
 * @author qbright
 *
 * @date 2013-1-19
 */
package me.qbright.lpms.web.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ServerMachine extends IdEntity {
	private String machineName;
	private Long belongTo;
	private String connection_ip;
	private String connection_port;
	private String system;
	private Date last_login;
    private String password;

	public static Map<String, String> SORTMAP = new HashMap<String, String>() {
	
		private static final long serialVersionUID = 1L;

		{
			put("connection_ip", "ip地址");
			put("system", "操作系统");
			put("last_login", "最后登录时间");
			put("machineName", "服务器名称");

		}
	};

	public String getMachineName() {
		return machineName;
	}

	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}

	public long getBelongTo() {
		return belongTo;
	}



	public String getConnection_ip() {
		return connection_ip;
	}

	public void setConnection_ip(String connection_ip) {
		this.connection_ip = connection_ip;
	}

	public String getConnection_port() {
		return connection_port;
	}

	public void setConnection_port(String connection_port) {
		this.connection_port = connection_port;
	}

	public String getSystem() {
		return system;
	}

	public void setSystem(String system) {
		this.system = system;
	}

	public Date getLast_login() {
		return last_login;
	}

	public void setLast_login(Date last_login) {
		this.last_login = last_login;
	}

	public void setBelongTo(Long belongTo) {
		this.belongTo = belongTo;
	}

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
	public String toString() {
		return "ServerMachine [machineName=" + machineName + ", belongTo="
				+ belongTo + ", connection_ip=" + connection_ip
				+ ", connection_port=" + connection_port + ", system=" + system
				+ "]";
	}

}

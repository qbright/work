/**
 * @author qbright
 *
 * @date 2013-1-19
 */
package me.qbright.lpms.web.entity;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User extends IdEntity {
	private String name;
	private String password;
	private String job_num;
	private String email;
	private Date create_date;
	private Integer status;
	private Integer manager_num;
	private Integer root;

	public static Map<String, String> SORTMAP = new HashMap<String, String>() {
	
		private static final long serialVersionUID = 1L;

		{
			put("name", "姓名");
			put("root", "角色");
			put("email", "电子邮件");
			put("manager_num", "管理机器数量");

		}
	};

	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getJob_num() {
		return job_num;
	}

	public void setJob_num(String job_num) {
		this.job_num = job_num;
	}

	public Date getCreate_date() {
		return create_date;
	}

	public void setCreate_date(Date create_date) {
		this.create_date = create_date;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Integer getManager_num() {
		return manager_num;
	}

	public void setManager_num(Integer manager_num) {
		this.manager_num = manager_num;
	}

	public Integer getRoot() {
		return root;
	}

	public void setRoot(Integer root) {
		this.root = root;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "User [name=" + name + ", job_num=" + job_num + ", manager_num="
				+ manager_num + "]";
	}

}

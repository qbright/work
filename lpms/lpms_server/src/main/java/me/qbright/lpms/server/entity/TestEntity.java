/**
 * @author qbright
 *
 * @date 2013-1-18
 */
package me.qbright.lpms.server.entity;

import java.util.Date;

public class TestEntity extends IdEntity {
	private String name;
	private Date birthday;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

}

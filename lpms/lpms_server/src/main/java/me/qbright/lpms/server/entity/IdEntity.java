/**
 * @author qbright
 *
 * @date 2013-1-18
 */
package me.qbright.lpms.server.entity;


public abstract class IdEntity {
	protected Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

}
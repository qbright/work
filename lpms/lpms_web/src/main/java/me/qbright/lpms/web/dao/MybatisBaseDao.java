/**
 * @author qbright
 *
 * @date 2013-2-4
 */
package me.qbright.lpms.web.dao;

import java.util.List;

import me.qbright.lpms.web.common.PageRequest;

public interface MybatisBaseDao<T> {
	void save(T entity);
	
	void delete(Long id);

	void update(T entity);

	long getTotalNum();

	T getById(Long id);

	T getByName(String entityName);

	T getByEntity(T entity);

	List<T> getAll();

	List<T> getByPage(PageRequest pageRequest);
}

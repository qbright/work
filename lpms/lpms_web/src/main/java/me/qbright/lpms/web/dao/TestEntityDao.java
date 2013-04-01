/**
 * @author qbright
 *
 * @date 2013-1-18
 */
package me.qbright.lpms.web.dao;

import me.qbright.lpms.web.entity.TestEntity;

@MyBatisRepository
public interface TestEntityDao {
	TestEntity getById(long id);

	void save(TestEntity testEntity);
}

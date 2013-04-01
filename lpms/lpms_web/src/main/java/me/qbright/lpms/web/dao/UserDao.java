/**
 * @author qbright
 *
 * @date 2013-1-19
 */
package me.qbright.lpms.web.dao;

import me.qbright.lpms.web.entity.User;

@MyBatisRepository
public interface UserDao extends MybatisBaseDao<User> {
	User checkLogin(User user);

	void updatePassword(User user);
	
	void updateMachine(User user);
}

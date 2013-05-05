/**
 * @author qbright
 *
 * @date 2013-1-23
 */
package me.qbright.lpms.web.service;

import me.qbright.lpms.web.common.Page;
import me.qbright.lpms.web.common.PageRequest;
import me.qbright.lpms.web.entity.User;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@Repository
public interface UserManagerService {
	Page<User> listByPage(PageRequest pageRequest);

	boolean checkUnique(User user);

	void save(User user);

	User getUser(long id);

	void update(User user);

	void deleteUser(Long id);

	void updatePassword(User user);
	
	void addMachine(Long id);
	
}

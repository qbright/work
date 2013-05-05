/**
 * @author qbright
 *
 * @date 2013-1-22
 */
package me.qbright.lpms.web.serviceimpl;

import me.qbright.lpms.web.dao.UserDao;
import me.qbright.lpms.web.entity.User;
import me.qbright.lpms.web.service.LoginService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("loginService")
public class LoginServiceImpl implements LoginService {

	@Autowired
	private UserDao userDao;

	@Override
	public User checkLogin(User user) {
		return userDao.checkLogin(user);
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}


}

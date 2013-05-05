/**
 * @author qbright
 *
 * @date 2013-1-18
 */
package me.qbright.lpms.web.serviceimpl;

import me.qbright.lpms.web.dao.ServerMachineDao;
import me.qbright.lpms.web.dao.TestEntityDao;
import me.qbright.lpms.web.dao.UserDao;
import me.qbright.lpms.web.service.TestService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("testService")
public class TestServiceImpl implements TestService {
	@Autowired
	private TestEntityDao testEntityDao;

	@Autowired
	private UserDao userDao;

	@Autowired
	private ServerMachineDao serverMachineDao;



	@Override
	public void testService() {
		// TODO Auto-generated method stub
		System.out.println(userDao.getAll().size());

	}

	public void setTestEntityDao(TestEntityDao testEntityDao) {
		this.testEntityDao = testEntityDao;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setServerMachineDao(ServerMachineDao serverMachineDao) {
		this.serverMachineDao = serverMachineDao;
	}



}

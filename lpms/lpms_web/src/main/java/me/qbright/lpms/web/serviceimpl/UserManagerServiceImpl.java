/**
 * @author qbright
 *
 * @date 2013-1-23
 */
package me.qbright.lpms.web.serviceimpl;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import me.qbright.lpms.common.EncodeCommon;
import me.qbright.lpms.web.common.Page;
import me.qbright.lpms.web.common.PageRequest;
import me.qbright.lpms.web.dao.UserDao;
import me.qbright.lpms.web.entity.Status;
import me.qbright.lpms.web.entity.User;
import me.qbright.lpms.web.service.UserManagerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("userManagerServer")
public class UserManagerServiceImpl implements UserManagerService {
	@Autowired
	private UserDao userdao;


	@Override
	public Page<User> listByPage(PageRequest pageRequest) {
		Page<User> page = new Page<User>(pageRequest, userdao.getTotalNum(),
				userdao.getByPage(PageRequest.build(pageRequest)), User.SORTMAP);
		return page;
	}


	@Override
	public boolean checkUnique(User user) {
		if (userdao.getByEntity(user) == null) {
			return true;
		} else {
			return false;
		}
	}

	public void setUserdao(UserDao userdao) {
		this.userdao = userdao;
	}


	@Override
	public void save(User user) {

		try {
			user.setPassword(EncodeCommon.digester(user.getPassword()));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		user.setCreate_date(new Date());
		user.setManager_num(0);
		user.setStatus(Status.ENABLE);
		userdao.save(user);
	}


	@Override
	public User getUser(long id) {
		return userdao.getById(id);
	}


	@Override
	public void update(User user) {
		userdao.update(user);
	}

	@Override
	public void deleteUser(Long id) {
		userdao.delete(id);
	}


	@Override
	public void updatePassword(User user) {
		userdao.updatePassword(user);
	}

	@Override
	public void addMachine(Long id) {
		User user = userdao.getById(id);
		user.setManager_num(user.getManager_num() + 1);
		userdao.updateMachine(user);
	}


	@Override
	public void deleteMachine(Long id) {
		User user = userdao.getById(id);
		user.setManager_num(user.getManager_num() - 1);
		userdao.updateMachine(user); 
		
	}

	
}

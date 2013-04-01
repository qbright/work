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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.qbright.lpms.web.service.UserManagerService#listByPage(me.qbright.
	 * lpms.web.common.PageRequest)
	 */
	@Override
	public Page<User> listByPage(PageRequest pageRequest) {
		// TODO Auto-generated method stub
		Page<User> page = new Page<User>(pageRequest, userdao.getTotalNum(),
				userdao.getByPage(PageRequest.build(pageRequest)), User.SORTMAP);
		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.qbright.lpms.web.service.UserManagerService#checkUnique(me.qbright
	 * .lpms.web.entity.User)
	 */
	@Override
	public boolean checkUnique(User user) {
		// TODO Auto-generated method stub
		if (userdao.getByEntity(user) == null) {
			return true;
		} else {
			return false;
		}
	}

	public void setUserdao(UserDao userdao) {
		this.userdao = userdao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.qbright.lpms.web.service.UserManagerService#save(me.qbright.lpms.web
	 * .entity.User)
	 */
	@Override
	public void save(User user) {
		// TODO Auto-generated method stub

		try {
			user.setPassword(EncodeCommon.digester(user.getPassword()));
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		user.setCreate_date(new Date());
		user.setManager_num(0);
		user.setStatus(Status.ENABLE);
		userdao.save(user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.qbright.lpms.web.service.UserManagerService#getUser(me.qbright.lpms
	 * .web.entity.User)
	 */
	@Override
	public User getUser(long id) {
		// TODO Auto-generated method stub
		return userdao.getById(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.qbright.lpms.web.service.UserManagerService#update(me.qbright.lpms
	 * .web.entity.User)
	 */
	@Override
	public void update(User user) {
		// TODO Auto-generated method stub
		userdao.update(user);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.qbright.lpms.web.service.UserManagerService#deleteUser(java.lang.Long)
	 */
	@Override
	public void deleteUser(Long id) {
		// TODO Auto-generated method stub
		userdao.delete(id);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.qbright.lpms.web.service.UserManagerService#updatePassword(me.qbright
	 * .lpms.web.entity.User)
	 */
	@Override
	public void updatePassword(User user) {
		// TODO Auto-generated method stub
		userdao.updatePassword(user);
	}

	@Override
	public void addMachine(Long id) {
		// TODO Auto-generated method stub
		User user = userdao.getById(id);
		user.setManager_num(user.getManager_num() + 1);
		userdao.updateMachine(user);
	}

}

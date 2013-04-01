/**
 * @author qbright
 *
 * @date 2013-1-31
 */
package me.qbright.lpms.web.serviceimpl;

import me.qbright.lpms.web.common.Page;
import me.qbright.lpms.web.common.PageRequest;
import me.qbright.lpms.web.dao.ServerMachineDao;
import me.qbright.lpms.web.entity.ServerMachine;
import me.qbright.lpms.web.service.ServerMachineManagerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("serverMachineManagerService")
public class ServerMachineManagerServiceImpl implements
		ServerMachineManagerService {
	@Autowired
	private ServerMachineDao serverMachineDao;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.qbright.lpms.web.service.ServerMachineManagerService#saveMachine(me
	 * .qbright.lpms.web.entity.ServerMachine)
	 */
	@Override
	public void saveMachine(ServerMachine serverMachine) {
		// TODO Auto
		serverMachineDao.save(serverMachine);
	}

	public void setServerMachineDao(ServerMachineDao serverMachineDao) {
		this.serverMachineDao = serverMachineDao;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.qbright.lpms.web.service.ServerMachineManagerService#checkUnique(me
	 * .qbright.lpms.web.entity.ServerMachine)
	 */
	@Override
	public boolean checkUnique(ServerMachine serverMachine) {
		// TODO Auto-generated method stub

		if (serverMachineDao.getByName(serverMachine.getMachineName()) == null) {
			return true;
		} else {
			return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.qbright.lpms.web.service.ServerMachineManagerService#listByPage(me
	 * .qbright.lpms.web.common.PageRequest)
	 */
	@Override
	public Page<ServerMachine> listByPage(PageRequest pageRequest, Long userId) {
		// TODO Auto-generated method stub
		Page<ServerMachine> page = new Page<ServerMachine>(pageRequest,
				serverMachineDao.getTotalNum(),
				serverMachineDao.getByPageUserId(
						PageRequest.build(pageRequest), userId),
				ServerMachine.SORTMAP);
		return page;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.qbright.lpms.web.service.ServerMachineManagerService#deleteMachine
	 * (me.qbright.lpms.web.entity.ServerMachine)
	 */
	@Override
	public void deleteMachine(ServerMachine serverMachine) {
		// TODO Auto-generated method stub
		serverMachineDao.delete(serverMachine.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.qbright.lpms.web.service.ServerMachineManagerService#getMachine(me
	 * .qbright.lpms.web.entity.ServerMachine)
	 */
	@Override
	public ServerMachine getMachine(ServerMachine serverMachine) {
		// TODO Auto-generated method stub
		return serverMachineDao.getById(serverMachine.getId());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * me.qbright.lpms.web.service.ServerMachineManagerService#updateMachine
	 * (me.qbright.lpms.web.entity.ServerMachine)
	 */
	@Override
	public void updateMachine(ServerMachine serverMachine) {
		// TODO Auto-generated method stub
		serverMachineDao.update(serverMachine);
	}

}

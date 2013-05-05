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
		ServerMachineManagerService{
	@Autowired
	private ServerMachineDao serverMachineDao;


	@Override
	public void saveMachine(ServerMachine serverMachine) {
		serverMachineDao.save(serverMachine);
	}

	public void setServerMachineDao(ServerMachineDao serverMachineDao) {
		this.serverMachineDao = serverMachineDao;
	}


	@Override
	public boolean checkUnique(ServerMachine serverMachine) {

		if (serverMachineDao.getByName(serverMachine.getMachineName()) == null) {
			return true;
		} else {
			return false;
		}
	}


	@Override
	public Page<ServerMachine> listByPage(PageRequest pageRequest, Long userId) {
		Page<ServerMachine> page = new Page<ServerMachine>(pageRequest,
				serverMachineDao.getTotalNum(),
				serverMachineDao.getByPageUserId(
						PageRequest.build(pageRequest), userId),
				ServerMachine.SORTMAP);
		return page;
	}


	@Override
	public void deleteMachine(ServerMachine serverMachine) {
		serverMachineDao.delete(serverMachine.getId());
	}


	@Override
	public ServerMachine getMachine(ServerMachine serverMachine) {
		return serverMachineDao.getById(serverMachine.getId());
	}

	@Override
	public void updateMachine(ServerMachine serverMachine) {
		serverMachineDao.update(serverMachine);
	}
	
	@Override
	public boolean checkAlive(Long id) {
		//TODO check Alive
		return true;
	}

}

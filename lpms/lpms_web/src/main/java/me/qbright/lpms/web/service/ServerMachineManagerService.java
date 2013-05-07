/**
 * @author qbright
 *
 * @date 2013-1-31
 */
package me.qbright.lpms.web.service;

import me.qbright.lpms.web.common.Page;
import me.qbright.lpms.web.common.PageRequest;
import me.qbright.lpms.web.entity.ServerMachine;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Repository
@Service
public interface ServerMachineManagerService {
	void saveMachine(ServerMachine serverMachine);

	boolean checkUnique(ServerMachine serverMachine);

	Page<ServerMachine> listByPage(PageRequest pageRequest, Long userId);

	void deleteMachine(ServerMachine serverMachine);

	ServerMachine getMachine(ServerMachine serverMachine);

	void updateMachine(ServerMachine serverMachine);

	boolean checkAlive(ServerMachine serverMachine);

}

/**
 * @author qbright
 *
 * @date 2013-2-7
 */
package me.qbright.lpms.web.service;

import java.util.Map;

import me.qbright.lpms.web.entity.ServerMachine;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

@Service
@Repository
public interface MonitorService {
	Map<String, String> getGeneralInfo(ServerMachine serverMachine);
}

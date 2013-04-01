/**
 * @author qbright
 *
 * @date 2013-1-19
 */
package me.qbright.lpms.web.dao;

import java.util.List;

import me.qbright.lpms.web.common.PageRequest;
import me.qbright.lpms.web.entity.ServerMachine;

import org.apache.ibatis.annotations.Param;

@MyBatisRepository
public interface ServerMachineDao extends MybatisBaseDao<ServerMachine> {
	List<ServerMachine> getByPageUserId(
			@Param("pageRequest") PageRequest pageRequest,
			@Param("userId") Long userid);

}

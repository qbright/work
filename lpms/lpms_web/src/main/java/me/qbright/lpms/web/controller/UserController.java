/**
 * @author qbright
 *
 * @date 2013-1-30
 */
package me.qbright.lpms.web.controller;

import java.io.UnsupportedEncodingException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.qbright.lpms.common.EncodeCommon;
import me.qbright.lpms.web.common.Page;
import me.qbright.lpms.web.common.PageRequest;
import me.qbright.lpms.web.entity.ServerMachine;
import me.qbright.lpms.web.entity.User;
import me.qbright.lpms.web.service.ServerMachineManagerService;
import me.qbright.lpms.web.service.UserManagerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "user")
public class UserController {

	@Autowired
	private ServerMachineManagerService serverMachineManagerService;

	@Autowired
	private UserManagerService userManagerService;

	@RequestMapping(value = "/manager_machine")
	public String getMechine(PageRequest pageRequest, Long id,
			HttpServletRequest request, HttpServletResponse response,
			Model model) {
		User user = (User) request.getSession().getAttribute("user");

		Page<ServerMachine> page = serverMachineManagerService.listByPage(
				pageRequest, user.getId());
		model.addAttribute(page);
		return "user_manager_serverMachine";
	}

	@RequestMapping(value = "/add")
	@ResponseBody
	public boolean saveMachine(ServerMachine serverMachine,
			HttpServletRequest request, HttpServletResponse response)
			throws UnsupportedEncodingException {

		User user = (User) request.getSession().getAttribute("user");
		serverMachine.setPassword(EncodeCommon.digester(serverMachine
				.getPassword()));
		serverMachine.setBelongTo(user.getId());
		serverMachineManagerService.saveMachine(serverMachine);
		userManagerService.addMachine(66L);
		return true;
	}

	@RequestMapping(value = "/checkUnique")
	@ResponseBody
	public boolean checkUnique(ServerMachine serverMachine) {
		return serverMachineManagerService.checkUnique(serverMachine);
	}

	@RequestMapping(value = "/saveChange")
	@ResponseBody
	public boolean saveChange(ServerMachine serverMachine) {
		serverMachineManagerService.updateMachine(serverMachine);
		return true;
	}

	@RequestMapping(value = "/deleteMachine")
	@ResponseBody
	public boolean deleteMachine(ServerMachine serverMachine) {
		serverMachineManagerService.deleteMachine(serverMachine);
		return true;

	}

	@RequestMapping(value = "/checkAlive")
	@ResponseBody
	public boolean checkAlive(ServerMachine serverMachine) {
		ServerMachine machine = serverMachineManagerService
				.getMachine(serverMachine);
		if (serverMachineManagerService.checkAlive(machine)) {
			machine.setLast_login(new Date());
			serverMachineManagerService.updateMachine(machine);
			return true;
		} else {
			return false;
		}
	}

	@RequestMapping(value = "/prepareUpdate")
	public String prepareUpdate(ServerMachine serverMachine, Model model) {
		model.addAttribute(serverMachineManagerService
				.getMachine(serverMachine));
		return "user_update_machine";
	}

}

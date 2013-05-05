package me.qbright.lpms.web.controller;

import java.util.Map;

import me.qbright.lpms.web.entity.ServerMachine;
import me.qbright.lpms.web.service.MonitorService;
import me.qbright.lpms.web.service.ServerMachineManagerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "machine")
public class MachineController {
	
	@Autowired
	private ServerMachineManagerService serverMachineManagerService;
	@Autowired
	private MonitorService monitorService;
	@RequestMapping("/generalInfo")
	public String generalInfo(ServerMachine serverMachine,Model model) {
		Map<String, String> generalInfo = monitorService.getGeneralInfo(serverMachineManagerService.getMachine(serverMachine));
		model.addAllAttributes(generalInfo);
		return "machine_generInfo";
	}
}

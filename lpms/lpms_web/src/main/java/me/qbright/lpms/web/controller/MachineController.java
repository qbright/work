package me.qbright.lpms.web.controller;

import java.util.List;
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
	public String generalInfo(ServerMachine serverMachine, Model model) {
		Map<String, Object> generalInfo = monitorService.getGeneralInfo(serverMachineManagerService.getMachine(serverMachine));
		model.addAllAttributes(generalInfo);
		return "machine_generalInfo";
	}
	
	@RequestMapping("/javaEnvironment")
	public String javaEnvironment(ServerMachine serverMachine,Model model){
		Map<String, Object> javaEnvironment = monitorService.getJavaEnvironment(serverMachineManagerService.getMachine(serverMachine));
		model.addAllAttributes(javaEnvironment);
		return "machine_javaEnvironment";
	}
	@RequestMapping("/fileSystemInfo")
	public String fileSystemInfo(ServerMachine serverMachine,Model model){
		List<Map<String, Object>> fileSystemInfo = monitorService.getFileSystemInfo(serverMachineManagerService.getMachine(serverMachine));
		model.addAttribute("fileSystemInfo_list", fileSystemInfo);
		return "machine_fileSystemInfo";
	}
	@RequestMapping("/netInfo")
	public String netInfo(ServerMachine serverMachine,Model model){
		List<Map<String, Object>> netInfo = monitorService.getNetInfo(serverMachineManagerService.getMachine(serverMachine));
		model.addAttribute("netInfo_list", netInfo);
		return "machine_netInfo";
	}
	@RequestMapping("/procInfo")
	public String procInfo(ServerMachine serverMachine,Model model){
		List<Map<String, Object>> procInfo = monitorService.getProcInfo(serverMachineManagerService.getMachine(serverMachine));
		model.addAttribute("procInfo_list", procInfo);
		return "machine_procInfo";
	}
}

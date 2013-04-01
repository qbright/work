/**
 * @author qbright
 *
 * @date 2013-1-24
 */
package me.qbright.lpms.web.controller;

import java.io.UnsupportedEncodingException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.qbright.lpms.common.EncodeCommon;
import me.qbright.lpms.web.common.Page;
import me.qbright.lpms.web.common.PageRequest;
import me.qbright.lpms.web.entity.User;
import me.qbright.lpms.web.service.UserManagerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "admin")
public class AdminController {

	@Autowired
	private UserManagerService userManagerService;

	@RequestMapping(value = "/manager_user")
	public String userManager(PageRequest pageRequest, Model model) {
		Page<User> page = userManagerService.listByPage(pageRequest);
		model.addAttribute(page);
		return "admin_manager_user";
	}

	@RequestMapping(value = "/checkUnique")
	@ResponseBody
	public boolean checkUnique(User user) {
		return userManagerService.checkUnique(user);
	}

	@RequestMapping(value = "/prepareUpdate")
	public String prepareUpdate(Long id, Model model) {
		model.addAttribute(userManagerService.getUser(id));
		return "admin_update_user";
	}

	@RequestMapping(value = "/saveChange")
	@ResponseBody
	public boolean saveChange(User user) {
		userManagerService.update(user);
		return true;
	}

	@RequestMapping(value = "/add")
	@ResponseBody
	public boolean add(User user) {
		userManagerService.save(user);
		return true;
	}

	@RequestMapping(value = "/deleteUser")
	@ResponseBody
	public boolean delete(Long id) {
		userManagerService.deleteUser(id);
		return true;
	}

	@RequestMapping(value = "/getSelf")
	public String getSelf(Model model, HttpServletRequest request,
			HttpServletResponse response) {
		User user = (User) request.getSession().getAttribute("user");

		model.addAttribute(user);
		return "admin_self";
	}

	@RequestMapping(value = "/checkPassword")
	@ResponseBody
	public boolean checkPassword(String oldPassword, HttpServletRequest request)
			throws UnsupportedEncodingException {
		User user = (User) request.getSession().getAttribute("user");
		if (EncodeCommon.digester(oldPassword).equals(user.getPassword())) {
			return true;
		} else {
			return false;
		}
	}

	@RequestMapping(value = "/changePassword")
	@ResponseBody
	public boolean changePassword(String password, HttpServletRequest request)
			throws UnsupportedEncodingException {
		System.out.println(password);
		User user = (User) request.getSession().getAttribute("user");
		user.setPassword(EncodeCommon.digester(password));
		userManagerService.updatePassword(user);
		return true;
	}
	public void setUserManagerService(UserManagerService userManagerService) {
		this.userManagerService = userManagerService;
	}

}

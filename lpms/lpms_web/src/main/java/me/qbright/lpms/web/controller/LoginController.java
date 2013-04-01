/**
 * @author qbright
 *
 * @date 2013-1-22
 */
package me.qbright.lpms.web.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import me.qbright.lpms.common.EncodeCommon;
import me.qbright.lpms.web.common.Page;
import me.qbright.lpms.web.entity.User;
import me.qbright.lpms.web.service.LoginService;
import me.qbright.lpms.web.service.UserManagerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "login")
public class LoginController {

	@Autowired
	private LoginService loginService;

	@Autowired
	private UserManagerService userManagerService;

	private Page<User> page;

	@RequestMapping
	public String login(Model model, User user,
 HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		user.setPassword(EncodeCommon.digester(user.getPassword()));
		user = loginService.checkLogin(user);
		if (user != null) {
			request.getSession().setAttribute("user", user);
			model.addAttribute(user);
			return "main";
		}else {
			request.setAttribute("error", 1);
			request.getRequestDispatcher("/").forward(request, response);
			return null;
		}
		
	}

	public void setLoginService(LoginService loginService) {
		this.loginService = loginService;
	}

	public Page<User> getPage() {
		return page;
	}

	public void setPage(Page<User> page) {
		this.page = page;
	}

	public void setUserManagerService(UserManagerService userManagerService) {
		this.userManagerService = userManagerService;
	}

}

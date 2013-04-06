package me.qbright.lpms.web.controller;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.qbright.lpms.web.common.Page;
import me.qbright.lpms.web.common.PageRequest;
import me.qbright.lpms.web.entity.TestEntity;
import me.qbright.lpms.web.entity.User;
import me.qbright.lpms.web.service.TestService;
import me.qbright.lpms.web.service.UserManagerService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;

/**
 * 
 * @author qbright
 * 
 */

@Controller
@RequestMapping(value = "/test")
public class TestController {
	@Autowired
	private TestService testService;

	@Autowired
	private UserManagerService userManagerService;

	private Page<User> page;

	@RequestMapping(method = RequestMethod.GET)
	public String test(Model model) {
		RestTemplate rt = new RestTemplate();

		List<HttpMessageConverter<?>> list = new ArrayList<HttpMessageConverter<?>>();
		list.add(new MappingJackson2HttpMessageConverter());
		rt.setMessageConverters(list);
		
		Map<String, Object> request = new HashMap<String, Object>();
		request.put("username", "qbright");
		request.put("password","1232345");
	
		TestEntity te = rt.postForObject(
				"http://localhost:8081/lpms_server/hello?username=123", null,TestEntity.class, request);
		System.out.println(te.getName());
		return " ";
	}

	@RequestMapping(value = "/tt", method = RequestMethod.GET)
	public String test_(Model model, PageRequest pageRequest)
			throws UnsupportedEncodingException {
		page = userManagerService.listByPage(pageRequest);
		return "main";
	}

	public void setTestService(TestService testService) {
		this.testService = testService;
	}

	public void setUserManagerService(UserManagerService userManagerService) {
		this.userManagerService = userManagerService;
	}

	public Page<User> getPage() {
		return page;
	}

	public void setPage(Page<User> page) {
		this.page = page;
	}



}

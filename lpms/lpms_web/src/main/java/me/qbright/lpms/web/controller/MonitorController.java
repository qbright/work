/**
 * @author qbright
 *
 * @date 2013-2-7
 */
package me.qbright.lpms.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "monitor")
public class MonitorController {

	@RequestMapping
	public String start() {
		System.out.println("start");
		return null;
	}
}

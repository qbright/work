package me.qbright.lpms.server.rest;

import me.qbright.lpms.server.entity.TestEntity;

import org.codehaus.jackson.map.ObjectMapper;

import za.co.softco.rest.model.Context;


/**
 * @author QBRIGHT
 * @date 2013-3-31
 */
@RestServiceHandler(serverName="TestHandler")
public class TestHandler {
	public void helloWorld(Context context) throws Exception {
		ObjectMapper om = new ObjectMapper();
		TestEntity te = new TestEntity();
		
		te.setName("hello I'm qbright");
		String s = om.writeValueAsString(te);
		System.out.println(s);
		
		RestServerExtend.writeJsonReply(context, s);
	}
}

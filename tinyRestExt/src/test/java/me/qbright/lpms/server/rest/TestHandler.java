package me.qbright.lpms.server.rest;

import java.lang.reflect.Method;



import za.co.softco.rest.model.Context;
import za.co.softco.rest.model.ResponseEvent;
import za.co.softco.rest.model.ResponseListener;


/**
 * @author QBRIGHT
 * @date 2013-3-31
 */
@RestServiceHandler(serverName="TestHandler")
public class TestHandler {
	public void helloWorld(Context context)  throws Exception {
		
		RestServerExtend.writeJsonReply(context, "hellow");
	}

	
}

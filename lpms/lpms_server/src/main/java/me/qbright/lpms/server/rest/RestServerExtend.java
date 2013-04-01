package me.qbright.lpms.server.rest;

import java.io.IOException;

import org.apache.log4j.Logger;

import za.co.softco.rest.model.Context;

/**
 * @anthor qiguang
 * @2013-4-1
 * @
 */
public class RestServerExtend {
	private static Logger logger = Logger.getLogger(RestServerExtend.class);
	private static final String CONTENT_TYPE_JSON = "application/json";
	private static final String DEFAULT_ENCODE = "UTF-8";

	public static void writeJsonReply(Context context, String content) {
		try {
			context.writeHeader(content.length(), CONTENT_TYPE_JSON, DEFAULT_ENCODE, null);
			context.write(content);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("Faile to sent the json :" + e);
		}
	}
}

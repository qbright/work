package me.qbright.lpms.server;

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;

import me.qbright.lpms.common.ConfigCommon;
import me.qbright.lpms.server.rest.RestService;



public class Main {
	public static void main(String[] args) throws JsonGenerationException, JsonMappingException, IOException {
		RestService restService = new RestService();
		restService.start(ConfigCommon.getKeyInt("SERVICE_PORT"),ConfigCommon.getKeyInt("MIN_WORKERS"), ConfigCommon.getKeyInt("MAX_WORKERS"));
		
	}
}

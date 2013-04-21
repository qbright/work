package me.qbright.lpms.server.info.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
/*package me.qbright.lpms.server.info.test;

import java.io.IOException;
import java.util.HashMap;
>>>>>>> 98b125fa2f947b4b5dc000c67f28f7eb5095348f
import java.util.Map;

import me.qbright.lpms.server.data.DataUtil;
import me.qbright.lpms.server.data.GeneralInfoData;

import org.apache.log4j.Logger;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
<<<<<<< HEAD
import org.junit.Test;

/**
 * @author QBRIGHT
 * @date 2013-4-21
 */

/**
 * @author QBRIGHT
 * @date 2013-4-21
 *//*
public class GeneralInfoTest {
	private static Logger log = Logger.getLogger(GeneralInfoTest.class);

	public void setUp() {
		System.setProperty("java.library.path", "target/classes/sigar-lib");// dev环境

	}
	@SuppressWarnings("unchecked")
	@Test
	public void testGeneralInfo() throws JsonGenerationException, JsonMappingException, IOException {
		TestClass c1 = new TestClass();
		c1.setName("a");
		c1.setPassword("b");
		TestClass c2 = new TestClass();
		c2.setName("c");
		c2.setPassword("d");
				
		List<TestClass> a = new ArrayList<TestClass>();
		
		a.add(c1);
		a.add(c2);
		
		Map<String, Object> m = new HashMap<String,Object>();
		
		m.put("list", a);
		m.put("name", "qbirght");
		
		ObjectMapper om = new ObjectMapper();
		
		System.out.println(om.writeValueAsString(m));
		
		Map<String, Object> m1 = new HashMap<String,Object>();
		m1 = om.readValue(om.writeValueAsString(m), Map.class);
		
		System.out.println(m1.get("list"));
	}
}




	public void testGeneralInfo() {
		GeneralInfoData generalInfoData = DataUtil.GENERAL_INFO_DATA;
		ObjectMapper om = new ObjectMapper();
		
		Map<String, String > map = new HashMap<String, String>();
		
		map.put("user",generalInfoData.realMemoryUsed());
		map.put("total", generalInfoData.realMemoryTotal());
		
		Map<String,Object > map1 = new HashMap<String, Object>();
		
		map1.put("realMemory", map);
		try {
			//log.info(om.writeValueAsString(map));
			log.info(om.writeValueAsString(map1));
			
		} catch (JsonGenerationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JsonMappingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info(generalInfoData.operationSystem());

		log.info(generalInfoData.timeOnSystem());

		log.info(generalInfoData.processorInfo());

		log.info(generalInfoData.sytemUptime());

		log.info(generalInfoData.runningProvesses());

		log.info(generalInfoData.cpuUsage());

		log.info(generalInfoData.realMemoryTotal());

		log.info(generalInfoData.realMemoryUsed());
		log.info(generalInfoData.localDiskTotal());

		log.info(generalInfoData.localDiskUsed());
	}
}
*/
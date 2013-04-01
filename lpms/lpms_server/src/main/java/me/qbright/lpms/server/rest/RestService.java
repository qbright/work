package me.qbright.lpms.server.rest;

import java.util.Set;

import me.qbright.lpms.common.ConfigCommon;
import me.qbright.lpms.common.PackageScanUtil;
import org.apache.log4j.Logger;

import za.co.softco.rest.DefaultRestFactory;
import za.co.softco.rest.ReflectionHandler;
import za.co.softco.rest.ReflectionService;
import za.co.softco.rest.RestFactory;
import za.co.softco.rest.RestServer;
import za.co.softco.rest.http.Compression;

/**
 * @author QBRIGHT
 * @date 2013-3-31
 * 
 *       对tinyRest 进行封装
 */
public class RestService {
	private RestServer restServer= null;

	private RestFactory factory;

	private ReflectionHandler handler;
	private static Logger logger = Logger.getLogger(RestService.class);

	public void start(int port, int minWorkers, int maxWorkers) {
		if (restServer != null) {
			logger.warn("service is running !!");

		} else {
			init(port, minWorkers, maxWorkers);
			Thread thread = new Thread(restServer);
			thread.setDaemon(true);
			thread.start();
		}
	}

	private void init(int port, int minWorkers, int maxWorker) {
		Compression compression = Compression.NONE;

		factory = new DefaultRestFactory();
		handler = new ReflectionHandler(
				ConfigCommon.getKeyString("SERVICE_ROOT"));
		factory.register(handler);
		registService();
		
		try {
			restServer= new RestServer(factory, port, minWorkers, maxWorker, true, false, compression);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("faile init the server :" + e);
		}
		
		

	}

	private void registService() {
		String packagePath = ConfigCommon.getKeyString("PACKAGE_PATH");
		if(packagePath == null){
			logger.error("packagePath is Empty");
			System.exit(0);
		}
		for (String packageName : packagePath.split(",")) {
			bindService(packageName);
		}
	}

	private void bindService(String packageName){
		PackageScanUtil psu = new PackageScanUtil();
		Set<Class<?>> classes = psu.getPackageAllClasses(packageName, false);
		
		for(Class<?> class_ : classes){
			if(class_.isAnnotationPresent(RestServiceHandler.class)){
				String serverName = class_.getAnnotation(RestServiceHandler.class).serverName();
				if(serverName == null || serverName.isEmpty()){
					logger.error(class_.getName() + "serverName is required ! "  );
					continue;
				}
				try {
					handler.register(new ReflectionService(serverName,class_.newInstance()));
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					logger.error("InstantiationException :" + e);
					continue;
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					logger.error("IllegalAccessException" + e);
				}
			}
		}
		
	}
}
package me.qbright.lpms.server.rest;

import java.util.Set;

import me.qbright.lpms.common.PackageScanUtil;

import org.apache.log4j.Logger;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Protocol;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;

/**
 * @author QBRIGHT
 * @date 2013-4-4
 * 简单的封装了restlet api
 */
public class RestletServer {
	private Component component;
	
	private static Logger logger = Logger.getLogger(RestletServer.class);
	/**
	 * 构造函数 
	 */
	public RestletServer(String serverRoot,int port,Restlet rootTarget) {
		 component = new Component();
		 Context context = new Context();
		 context.getParameters().set("maxThreads", "100");
		 context.getParameters().set("maxConnectionsPerHost", "100"); 
		 context.getParameters().set("maxTotalConnections", "100"); 
		 Server server = new Server(context, Protocol.HTTP,port);
		 component.getServers().add(server);
		 component.getDefaultHost().attach(serverRoot, rootTarget);
	}
	
	
	public void start(){
		try {
			component.start();
		} catch (Exception e) {
			logger.error("faile to boot the server", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	public static Router bindServerResource(Router router,String pathName){
		PackageScanUtil psu  = new PackageScanUtil();
		Set<Class<?>> classes = psu.getPackageAllClasses(pathName, false);
		
		for(Class<?> class_ : classes){
			if(class_.isAnnotationPresent(RequestHandler.class)){
				String serverName = class_.getAnnotation(RequestHandler.class).serverName();
				
				if(serverName == null || serverName.isEmpty()){
					logger.error(class_.getName() + "serverName is required ! ");
					continue;
				}
				router.attach(serverName, (Class<? extends ServerResource>) class_);
			}
		}
		return router;
	}
	
}
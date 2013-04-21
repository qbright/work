package me.qbright.lpms.server.rest;


import org.restlet.Application;

import org.restlet.Restlet;
import org.restlet.routing.Router;

/**
 * @author QBRIGHT
 * @date 2013-4-5
 * rest服务入口，配置了检查权限的filter,检查完权限跳到RequestHandlerRoot
 */
public class RootRestlet extends Application{

	/* (non-Javadoc)
	 * @see org.restlet.Application#createInboundRoot()
	 */
	@Override
	public Restlet createInboundRoot() {
			Router router = new Router(getContext());
			RequestHandlerRoot root = new RequestHandlerRoot();
			AuthorizedBlock authorizedBlock = new AuthorizedBlock(getContext());
			authorizedBlock.setNext(root);
			
			router.attach("",authorizedBlock, Router.MODE_BEST_MATCH);
			
		
		// TODO Auto-generated method stub
		return  router;
	}

	

		
}

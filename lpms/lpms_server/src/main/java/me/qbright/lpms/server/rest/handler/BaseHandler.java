package me.qbright.lpms.server.rest.handler;


import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * @author QBRIGHT
 * @date 2013-4-22
 */
public interface BaseHandler {
	static Logger log = Logger.getLogger(Thread.currentThread().getContextClassLoader().getClass());
	static ObjectMapper om = new ObjectMapper();
	@Post
	String getContent();
}

/**
 * @author qbright
 * 2013-4-14
 */
package me.qbright.lpms.web.restclient;

import java.io.IOException;

import org.restlet.data.Form;
import org.restlet.data.Method;
import org.restlet.data.Reference;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;

/**
 * @author QBRIGHT
 * @date 2013-4-14
 */
public class RestClient {
	
	private ClientResource client;
	
	
	public RestClient(Method method,String url,Object requestObject,Class<?> responseClass) throws ResourceException, IOException {
		// TODO Auto-generated constructor stub

	}
	public static void main(String[] args) throws ResourceException, IOException {
		RestClient rc = new RestClient(Method.POST, "", null, String.class);
	}
}

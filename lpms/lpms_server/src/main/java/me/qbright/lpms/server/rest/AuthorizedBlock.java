/**
 * @author qbright
 * 2013-4-5
 */
package me.qbright.lpms.server.rest;


import org.apache.log4j.Logger;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Form;
import org.restlet.routing.Filter;

/**
 * @author QBRIGHT
 * @date 2013-4-5
 */
public class AuthorizedBlock extends Filter{
	
	private static Logger logger = Logger.getLogger(AuthorizedBlock.class);
	
	
	
	/**
	 * 
	 */
	public AuthorizedBlock(Context context) {
		// TODO Auto-generated constructor stub
		setContext(context);
	}
	/* (non-Javadoc)
	 * @see org.restlet.routing.Filter#beforeHandle(org.restlet.Request, org.restlet.Response)
	 */
	@Override
	protected int beforeHandle(Request request, Response response) {
		// TODO Auto-generated method stub
		int result = CONTINUE;
		
		//Form form = new Form(request.getEntity());

		

		//System.out.println(form.getFirstValue("username"));
		//String query = request.getResourceRef().getQueryAsForm().getFirstValue("username");
		//logger.info(query);
		return result;
	}

	/* (non-Javadoc)
	 * @see org.restlet.routing.Filter#setNext(org.restlet.Restlet)
	 */
	@Override
	public void setNext(Restlet next) {
		// TODO Auto-generated method stub
		super.setNext(next);
	}
	
	
}

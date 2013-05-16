package me.qbright.lpms.server.rest;


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
	
	//private static Logger logger = Logger.getLogger(AuthorizedBlock.class);
	
	
	

	public AuthorizedBlock(Context context) {
		setContext(context);
	}

	@Override
	protected int beforeHandle(Request request, Response response) {
		int result = CONTINUE;
		int stop = STOP;
		Form form = new Form(request.getEntity());
			String machineName = form.getFirstValue("machineName");
		String machinePassword = form.getFirstValue("machinePassword");
		if(machineName == null || machinePassword == null || !machineName.equals(System.getProperty("machineName")) || !machinePassword.equals(System.getProperty("machinePassword"))){
				return stop;
		}else{
			return result;
		}
	}

	@Override
	public void setNext(Restlet next) {
		super.setNext(next);
	}
	
	
}

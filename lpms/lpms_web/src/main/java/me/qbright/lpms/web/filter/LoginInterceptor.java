/**
 * @author qbright
 *
 * @date 2013-1-30
 */
package me.qbright.lpms.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class LoginInterceptor implements Filter {


	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}


	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		// TODO Auto-generated method stub
		System.out.println("filter");
		chain.doFilter(request, response);
	}


	@Override
	public void destroy() {
		// TODO Auto-generated method stub

	}

}

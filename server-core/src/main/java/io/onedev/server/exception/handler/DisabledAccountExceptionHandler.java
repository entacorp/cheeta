package io.cheeta.server.exception.handler;

import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authc.DisabledAccountException;

import io.cheeta.server.exception.HttpResponse;

public class DisabledAccountExceptionHandler extends AbstractExceptionHandler<DisabledAccountException> {
	
	private static final long serialVersionUID = 1L;

	@Override
    public HttpResponse getResponse(DisabledAccountException exception) {
		return new HttpResponse(HttpServletResponse.SC_UNAUTHORIZED,  
				"Account is disabled");
    }
    
}

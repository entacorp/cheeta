package io.cheeta.server.web.exceptionhandler;

import io.cheeta.server.exception.HttpResponse;
import io.cheeta.server.exception.handler.AbstractExceptionHandler;
import org.apache.wicket.protocol.http.PageExpiredException;

import javax.servlet.http.HttpServletResponse;

public class PageExpiredExceptionHandler extends AbstractExceptionHandler<PageExpiredException> {
	
	private static final long serialVersionUID = 1L;

	@Override
    public HttpResponse getResponse(PageExpiredException exception) {
		String errorMessage = exception.getMessage();
		if (errorMessage == null)
			errorMessage = "Page expired";
		return new HttpResponse(HttpServletResponse.SC_REQUEST_TIMEOUT, errorMessage);
    }
    
}

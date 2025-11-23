package io.cheeta.server.exception.handler;

import io.cheeta.commons.utils.ExplicitException;
import io.cheeta.server.exception.HttpResponse;

import javax.servlet.http.HttpServletResponse;

public class ExplicitExceptionHandler extends AbstractExceptionHandler<ExplicitException> {
	
	private static final long serialVersionUID = 1L;

	@Override
    public HttpResponse getResponse(ExplicitException exception) {
		return new HttpResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, exception.getMessage());
    }
    
}

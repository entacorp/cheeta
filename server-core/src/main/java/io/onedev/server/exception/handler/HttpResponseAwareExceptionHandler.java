package io.cheeta.server.exception.handler;

import io.cheeta.server.exception.HttpResponse;
import io.cheeta.server.exception.HttpResponseAwareException;

public class HttpResponseAwareExceptionHandler extends AbstractExceptionHandler<HttpResponseAwareException> {
	
	private static final long serialVersionUID = 1L;

	@Override
    public HttpResponse getResponse(HttpResponseAwareException exception) {
		return exception.getHttpResponse();
    }
    
}

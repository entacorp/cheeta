package io.cheeta.server.persistence.exception;

import io.cheeta.server.exception.HttpResponse;
import io.cheeta.server.exception.handler.AbstractExceptionHandler;
import org.hibernate.exception.ConstraintViolationException;

import javax.servlet.http.HttpServletResponse;

public class ConstraintViolationExceptionHandler extends AbstractExceptionHandler<ConstraintViolationException> {
	
	private static final long serialVersionUID = 1L;

	@Override
    public HttpResponse getResponse(ConstraintViolationException exception) {
		var errorMessage = exception.getMessage();
		if (errorMessage == null)
			errorMessage = "Database constraint violation";
		return new HttpResponse(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, errorMessage);
    }
    
}

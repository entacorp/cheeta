package io.cheeta.server.exception.handler;

import io.cheeta.server.exception.HttpResponse;

import java.io.Serializable;

public interface ExceptionHandler<T extends Throwable> extends Serializable {
	
	HttpResponse getResponse(T throwable);
	
	Class<T> getExceptionClass();
	
}

package io.cheeta.server.plugin.pack.pypi;

import io.cheeta.server.exception.HttpResponseAwareException;

public class ClientException extends HttpResponseAwareException {

	public ClientException(int statusCode, String errorMessage) {
		super(statusCode, errorMessage);
	}

	public ClientException(int statusCode) {
		super(statusCode);
	}
	
}

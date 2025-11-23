package io.cheeta.server.plugin.pack.nuget;

import io.cheeta.server.exception.HttpResponseAwareException;

public class ClientException extends HttpResponseAwareException {

	public ClientException(int statusCode) {
		super(statusCode);
	}
	
}

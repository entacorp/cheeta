package io.cheeta.server.git.exception;

import io.cheeta.commons.utils.ExplicitException;

public class NotTreeException extends ExplicitException {

	private static final long serialVersionUID = 1L;

	public NotTreeException(String message) {
		super(message);
	}
	
}

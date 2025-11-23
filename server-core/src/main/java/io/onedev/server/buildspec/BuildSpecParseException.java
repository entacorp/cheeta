package io.cheeta.server.buildspec;

import io.cheeta.commons.utils.ExplicitException;

public class BuildSpecParseException extends ExplicitException {

	private static final long serialVersionUID = 1L;

	public BuildSpecParseException(String message, Throwable cause) {
		super(message, cause);
	}

}

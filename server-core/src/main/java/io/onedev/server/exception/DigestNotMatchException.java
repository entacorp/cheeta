package io.cheeta.server.exception;

import io.cheeta.commons.utils.ExplicitException;

public class DigestNotMatchException extends ExplicitException {
	
	public DigestNotMatchException() {
		super("Digest not matching");
	}
	
}

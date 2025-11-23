package io.cheeta.server.exception;

import io.cheeta.commons.utils.ExplicitException;

public class DataTooLargeException extends ExplicitException {
	public DataTooLargeException(long maxSize) {
		super("Data exceeds maximum size: " + maxSize);
	}
}

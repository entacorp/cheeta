package io.cheeta.server.buildspec;

import io.cheeta.commons.utils.ExplicitException;
import io.cheeta.server.util.Path;

public class BuildSpecException extends ExplicitException {

	private static final long serialVersionUID = 1L;

	public BuildSpecException(Path path, String message) {
		super(path.toString() + ": " + message);
	}
	
}

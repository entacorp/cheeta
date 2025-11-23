package io.cheeta.server.exception;

import io.cheeta.commons.utils.ExplicitException;

public class NoSubscriptionException extends ExplicitException {
	
	public NoSubscriptionException(String feature) {
		super(feature + " requires an active subscription");
	}
	
}

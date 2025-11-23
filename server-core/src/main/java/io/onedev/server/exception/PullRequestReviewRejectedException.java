package io.cheeta.server.exception;

import io.cheeta.commons.utils.ExplicitException;

public class PullRequestReviewRejectedException extends ExplicitException {

	public PullRequestReviewRejectedException(String message) {
		super(message);		
	}

}
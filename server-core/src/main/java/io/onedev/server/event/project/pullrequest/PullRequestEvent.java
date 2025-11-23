package io.cheeta.server.event.project.pullrequest;

import java.util.Date;

import org.jspecify.annotations.Nullable;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.PullRequestService;
import io.cheeta.server.web.UrlService;
import io.cheeta.server.event.project.ProjectEvent;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.User;

public abstract class PullRequestEvent extends ProjectEvent {

	private static final long serialVersionUID = 1L;
	
	private final Long requestId;
	
	public PullRequestEvent(@Nullable User user, Date date, PullRequest request) {
		super(user, date, request.getTargetProject());
		requestId = request.getId();
	}

	public PullRequest getRequest() {
		return Cheeta.getInstance(PullRequestService.class).load(requestId);
	}

	@Override
	public String getLockName() {
		return PullRequest.getSerialLockName(requestId);
	}
	
	@Override
	public String getUrl() {
		return Cheeta.getInstance(UrlService.class).urlFor(getRequest(), true);
	}
	
}

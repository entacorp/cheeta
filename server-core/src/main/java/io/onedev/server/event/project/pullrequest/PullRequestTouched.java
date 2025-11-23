package io.cheeta.server.event.project.pullrequest;

import java.util.Date;

import io.cheeta.server.event.project.ProjectEvent;
import io.cheeta.server.model.Project;
import io.cheeta.server.security.SecurityUtils;

public class PullRequestTouched extends ProjectEvent {
	
	private static final long serialVersionUID = 1L;
	
	private final Long requestId;
	
	public PullRequestTouched(Project project, Long requestId) {
		super(SecurityUtils.getUser(), new Date(), project);
		this.requestId = requestId;
	}
	
	public Long getRequestId() {
		return requestId;
	}

	@Override
	public String getActivity() {
		return "touched";
	}
	
}

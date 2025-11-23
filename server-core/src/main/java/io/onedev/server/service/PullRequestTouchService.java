package io.cheeta.server.service;

import java.util.List;

import io.cheeta.server.model.Project;
import io.cheeta.server.model.PullRequestTouch;

public interface PullRequestTouchService extends EntityService<PullRequestTouch> {
	
	void touch(Project project, Long requestId, boolean newRequest);
	
	List<PullRequestTouch> queryTouchesAfter(Long projectId, Long afterTouchId, int count);
	
}

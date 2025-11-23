package io.cheeta.server.service;

import java.util.List;

import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.PullRequestUpdate;

public interface PullRequestUpdateService extends EntityService<PullRequestUpdate> {
	
	void checkUpdate(PullRequest request);
	
	List<PullRequestUpdate> queryAfter(Long projectId, Long afterUpdateId, int count);

    void create(PullRequestUpdate update);
	
}

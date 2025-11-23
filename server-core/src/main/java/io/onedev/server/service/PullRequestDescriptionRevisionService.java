package io.cheeta.server.service;

import io.cheeta.server.model.PullRequestDescriptionRevision;

public interface PullRequestDescriptionRevisionService extends EntityService<PullRequestDescriptionRevision> {
		
	void create(PullRequestDescriptionRevision revision);
	
}

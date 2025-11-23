package io.cheeta.server.service;

import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.PullRequestLabel;

import java.util.Collection;

public interface PullRequestLabelService extends EntityLabelService<PullRequestLabel> {

	void create(PullRequestLabel pullRequestLabel);
	
	void populateLabels(Collection<PullRequest> pullRequests);
	
}

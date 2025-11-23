package io.cheeta.server.service;

import io.cheeta.server.model.Project;
import io.cheeta.server.model.PullRequestQueryPersonalization;
import io.cheeta.server.model.User;

public interface PullRequestQueryPersonalizationService extends EntityService<PullRequestQueryPersonalization> {
	
	PullRequestQueryPersonalization find(Project project, User user);

    void createOrUpdate(PullRequestQueryPersonalization personalization);
	
    void delete(PullRequestQueryPersonalization personalization);
}

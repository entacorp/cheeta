package io.cheeta.server.service;

import io.cheeta.server.model.CommitQueryPersonalization;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.User;

public interface CommitQueryPersonalizationService extends EntityService<CommitQueryPersonalization> {
	
	CommitQueryPersonalization find(Project project, User user);

    void createOrUpdate(CommitQueryPersonalization commitQueryPersonalization);
	
}

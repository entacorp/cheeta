package io.cheeta.server.service;

import io.cheeta.server.model.IssueQueryPersonalization;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.User;
import io.cheeta.server.util.ProjectScope;

import java.util.Collection;

public interface IssueQueryPersonalizationService extends EntityService<IssueQueryPersonalization> {
	
	IssueQueryPersonalization find(Project project, User user);

    void createOrUpdate(IssueQueryPersonalization personalization);
	
	Collection<IssueQueryPersonalization> query(ProjectScope projectScope);
	
}

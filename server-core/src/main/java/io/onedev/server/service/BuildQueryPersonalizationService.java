package io.cheeta.server.service;

import io.cheeta.server.model.BuildQueryPersonalization;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.User;

public interface BuildQueryPersonalizationService extends EntityService<BuildQueryPersonalization> {
	
	BuildQueryPersonalization find(Project project, User user);

    void createOrUpdate(BuildQueryPersonalization buildQueryPersonalization);
	
}

package io.cheeta.server.service;

import io.cheeta.server.model.PackQueryPersonalization;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.User;

public interface PackQueryPersonalizationService extends EntityService<PackQueryPersonalization> {
	
	PackQueryPersonalization find(Project project, User user);

    void createOrUpdate(PackQueryPersonalization personalization);
	
}

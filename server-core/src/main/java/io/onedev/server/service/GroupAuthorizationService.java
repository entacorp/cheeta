package io.cheeta.server.service;

import io.cheeta.server.model.Group;
import io.cheeta.server.model.GroupAuthorization;
import io.cheeta.server.model.Project;

import java.util.Collection;

public interface GroupAuthorizationService extends EntityService<GroupAuthorization> {

	void syncAuthorizations(Group group, Collection<GroupAuthorization> authorizations);

	void syncAuthorizations(Project project, Collection<GroupAuthorization> authorizations);
	
    void createOrUpdate(GroupAuthorization authorization);
	
}

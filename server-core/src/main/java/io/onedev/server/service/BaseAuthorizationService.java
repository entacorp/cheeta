package io.cheeta.server.service;

import java.util.Collection;

import io.cheeta.server.model.BaseAuthorization;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.Role;

public interface BaseAuthorizationService extends EntityService<BaseAuthorization> {

	void syncRoles(Project project, Collection<Role> roles);
			
	void create(BaseAuthorization authorization);
	
}

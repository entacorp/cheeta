package io.cheeta.server.service;

import java.util.Collection;

import io.cheeta.server.model.Project;
import io.cheeta.server.model.User;
import io.cheeta.server.model.UserAuthorization;

public interface UserAuthorizationService extends EntityService<UserAuthorization> {

	void syncAuthorizations(User user, Collection<UserAuthorization> authorizations);
	
	void syncAuthorizations(Project project, Collection<UserAuthorization> authorizations);
	
    void createOrUpdate(UserAuthorization authorization);
	
}
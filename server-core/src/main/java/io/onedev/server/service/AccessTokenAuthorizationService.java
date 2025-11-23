package io.cheeta.server.service;

import io.cheeta.server.model.AccessToken;
import io.cheeta.server.model.AccessTokenAuthorization;

import java.util.Collection;

public interface AccessTokenAuthorizationService extends EntityService<AccessTokenAuthorization> {

	void syncAuthorizations(AccessToken token, Collection<AccessTokenAuthorization> authorizations);
	
    void createOrUpdate(AccessTokenAuthorization authorization);
	
}
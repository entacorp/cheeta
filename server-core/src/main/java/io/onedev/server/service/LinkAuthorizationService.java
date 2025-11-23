package io.cheeta.server.service;

import java.util.Collection;

import io.cheeta.server.model.LinkAuthorization;
import io.cheeta.server.model.LinkSpec;
import io.cheeta.server.model.Role;

public interface LinkAuthorizationService extends EntityService<LinkAuthorization> {

	void syncAuthorizations(Role role, Collection<LinkSpec> authorizedLinks);

    void create(LinkAuthorization authorization);

}

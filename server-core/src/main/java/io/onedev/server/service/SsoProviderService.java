package io.cheeta.server.service;

import org.jspecify.annotations.Nullable;

import io.cheeta.server.model.SsoProvider;

public interface SsoProviderService extends EntityService<SsoProvider> {
	
	void createOrUpdate(SsoProvider ssoProvider);
		
	@Nullable
	SsoProvider find(String name);

}

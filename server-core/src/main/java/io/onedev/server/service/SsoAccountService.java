package io.cheeta.server.service;

import org.jspecify.annotations.Nullable;

import io.cheeta.server.model.SsoAccount;
import io.cheeta.server.model.SsoProvider;

public interface SsoAccountService extends EntityService<SsoAccount> {
	
	void create(SsoAccount ssoAccount);
		
	@Nullable
	SsoAccount find(SsoProvider provider, String subject);

}

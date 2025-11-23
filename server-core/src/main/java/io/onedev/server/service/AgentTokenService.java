package io.cheeta.server.service;

import io.cheeta.server.model.AgentToken;

import org.jspecify.annotations.Nullable;
import java.util.List;

public interface AgentTokenService extends EntityService<AgentToken> {
	
	void createOrUpdate(AgentToken token);
	
	@Nullable
	AgentToken find(String value);
	
	List<AgentToken> queryUnused();
	
	void deleteUnused();
	
}

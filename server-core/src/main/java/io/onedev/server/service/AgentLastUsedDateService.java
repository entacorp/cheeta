package io.cheeta.server.service;

import io.cheeta.server.model.AgentLastUsedDate;

public interface AgentLastUsedDateService extends EntityService<AgentLastUsedDate> {

	void create(AgentLastUsedDate lastUsedDate);
	
}

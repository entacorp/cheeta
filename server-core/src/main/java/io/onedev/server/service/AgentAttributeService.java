package io.cheeta.server.service;

import io.cheeta.server.model.Agent;
import io.cheeta.server.model.AgentAttribute;

import java.util.Collection;
import java.util.Map;

public interface AgentAttributeService extends EntityService<AgentAttribute> {

	void create(AgentAttribute attribute);
	
	Collection<String> getAttributeNames();

	void syncAttributes(Agent agent, Map<String, String> attributeMap);
	
}

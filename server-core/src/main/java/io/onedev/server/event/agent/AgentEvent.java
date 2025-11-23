package io.cheeta.server.event.agent;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.AgentService;
import io.cheeta.server.event.Event;
import io.cheeta.server.model.Agent;

public abstract class AgentEvent extends Event {
	
	private final Long agentId;
	
	public AgentEvent(Agent agent) {
		agentId = agent.getId();
	}

	public Agent getAgent() {
		return Cheeta.getInstance(AgentService.class).load(agentId);
	}
	
}

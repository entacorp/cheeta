package io.cheeta.server.event.agent;

import io.cheeta.server.model.Agent;

public class AgentDisconnected extends AgentEvent {
	
	public AgentDisconnected(Agent agent) {
		super(agent);
	}

}

package io.cheeta.server.event.agent;

import io.cheeta.server.model.Agent;

public class AgentConnected extends AgentEvent {
	
	public AgentConnected(Agent agent) {
		super(agent);
	}

}

package io.cheeta.server.job;

import java.io.Serializable;

public interface AgentRunnable extends Serializable {
	
	boolean run(Long agentId);
	
}

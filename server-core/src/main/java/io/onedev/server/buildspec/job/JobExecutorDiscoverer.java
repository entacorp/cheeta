package io.cheeta.server.buildspec.job;

import org.jspecify.annotations.Nullable;

import io.cheeta.server.model.support.administration.jobexecutor.JobExecutor;

public interface JobExecutorDiscoverer {
	
	@Nullable
	JobExecutor discover();
	
	int getOrder();
	
}

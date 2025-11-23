package io.cheeta.server.job.log;

import org.jspecify.annotations.Nullable;

import io.cheeta.commons.utils.TaskLogger;
import io.cheeta.server.Cheeta;
import io.cheeta.server.cluster.ClusterTask;

public class LogTask implements ClusterTask<Void> {

	private static final long serialVersionUID = 1L;

	private final String jobToken;
	
	private final String message;
	
	private final String sessionId;
	
	public LogTask(String jobToken, String message, @Nullable String sessionId) {
		this.jobToken = jobToken;
		this.message = message;
		this.sessionId = sessionId;
	}
	
	@Override
	public Void call() {
		TaskLogger logger = Cheeta.getInstance(LogService.class).getJobLogger(jobToken);
		if (logger != null && !(logger instanceof ServerJobLogger))  
			logger.log(message, sessionId);
		return null;
	}
	
}
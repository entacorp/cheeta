package io.cheeta.server.job.log;

import io.cheeta.commons.utils.TaskLogger;
import io.cheeta.server.Cheeta;
import io.cheeta.server.cluster.ClusterService;
import org.jetbrains.annotations.Nullable;

public class ServerJobLogger extends TaskLogger {
	
	private final String server;
	
	private final String jobToken;
	
	public ServerJobLogger(String server, String jobToken) {
		this.server = server;
		this.jobToken = jobToken;
	}
	
	@Override
	public void log(String message, @Nullable String sessionId) {
		var clusterService = Cheeta.getInstance(ClusterService.class);
		clusterService.runOnServer(server, new LogTask(jobToken, message, sessionId));	
	}
	
}

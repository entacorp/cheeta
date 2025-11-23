package io.cheeta.server.job.log;

public interface LogListener {
	
	void logged(Long buildId);
	
}

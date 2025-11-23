package io.cheeta.server.buildspec.job;

import org.jspecify.annotations.Nullable;

import io.cheeta.server.buildspec.ParamSpecAware;

public interface JobAware extends ParamSpecAware {
	
	@Nullable
	Job getJob();
	
}

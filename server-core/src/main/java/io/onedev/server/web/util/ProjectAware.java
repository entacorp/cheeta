package io.cheeta.server.web.util;

import io.cheeta.server.model.Project;

import org.jspecify.annotations.Nullable;

public interface ProjectAware {
	
	@Nullable
	Project getProject();
	
}

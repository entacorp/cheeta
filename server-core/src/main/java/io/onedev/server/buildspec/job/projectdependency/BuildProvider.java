package io.cheeta.server.buildspec.job.projectdependency;

import java.io.Serializable;

import org.jspecify.annotations.Nullable;

import io.cheeta.server.model.Build;
import io.cheeta.server.model.Project;
import io.cheeta.server.annotation.Editable;

@Editable
public interface BuildProvider extends Serializable {

	@Nullable
	Build getBuild(Project project);
	
	String getDescription();
}

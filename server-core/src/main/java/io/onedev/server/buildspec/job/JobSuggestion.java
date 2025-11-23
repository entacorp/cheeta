package io.cheeta.server.buildspec.job;

import java.util.Collection;

import org.eclipse.jgit.lib.ObjectId;

import io.cheeta.commons.loader.ExtensionPoint;
import io.cheeta.server.model.Project;

@ExtensionPoint
public interface JobSuggestion {
	
	Collection<Job> suggestJobs(Project project, ObjectId commitId);

}

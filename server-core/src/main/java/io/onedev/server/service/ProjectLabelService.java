package io.cheeta.server.service;

import io.cheeta.server.model.Project;
import io.cheeta.server.model.ProjectLabel;

import java.util.Collection;

public interface ProjectLabelService extends EntityLabelService<ProjectLabel> {
	
	void create(ProjectLabel projectLabel);

	void populateLabels(Collection<Project> projects);
	
}

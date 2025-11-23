package io.cheeta.server.service;

import io.cheeta.server.model.ProjectLastActivityDate;

public interface ProjectLastEventDateService extends EntityService<ProjectLastActivityDate> {

	void create(ProjectLastActivityDate lastEventDate);
	
}

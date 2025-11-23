package io.cheeta.server.service.impl;

import java.util.Date;

import javax.inject.Singleton;

import com.google.common.base.Preconditions;

import io.cheeta.server.event.Listen;
import io.cheeta.server.event.project.ProjectCreated;
import io.cheeta.server.event.project.ProjectEvent;
import io.cheeta.server.event.project.RefUpdated;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.ProjectLastActivityDate;
import io.cheeta.server.persistence.annotation.Transactional;
import io.cheeta.server.service.ProjectLastEventDateService;

@Singleton
public class DefaultProjectLastEventDateService extends BaseEntityService<ProjectLastActivityDate> implements ProjectLastEventDateService {

	@Transactional
	@Listen
	public void on(ProjectEvent event) {
		Project project = event.getProject();
		if (event instanceof RefUpdated) {
			project.getLastActivityDate().setValue(new Date());
		} else if (!(event instanceof ProjectCreated) 
				&& event.getUser() != null 
				&& !event.getUser().isSystem()) {
			project.getLastActivityDate().setValue(new Date());
		}
	}
	
	@Transactional
	@Override
	public void create(ProjectLastActivityDate lastEventDate) {
		Preconditions.checkState(lastEventDate.isNew());
		dao.persist(lastEventDate);
	}
	
}

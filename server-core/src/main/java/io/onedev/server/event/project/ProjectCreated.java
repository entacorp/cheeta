package io.cheeta.server.event.project;

import java.util.Date;

import io.cheeta.server.model.Project;
import io.cheeta.server.security.SecurityUtils;

public class ProjectCreated extends ProjectEvent {
	
	public ProjectCreated(Project project) {
		super(SecurityUtils.getUser(), new Date(), project);
	}

	@Override
	public String getActivity() {
		return "created";
	}

}

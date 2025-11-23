package io.cheeta.server.event.project.issue;

import io.cheeta.server.Cheeta;
import io.cheeta.server.event.project.ProjectEvent;
import io.cheeta.server.model.Issue;
import io.cheeta.server.model.Project;
import io.cheeta.server.persistence.dao.Dao;
import io.cheeta.server.security.SecurityUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class IssuesCopied extends ProjectEvent {
	
	private static final long serialVersionUID = 1L;
	
	private final Long sourceProjectId;
	
	private final Map<Long, Long> issueIdMapping;
	
	public IssuesCopied(Project sourceProject, Project targetProject, Map<Issue, Issue> issueMapping) {
		super(SecurityUtils.getUser(), new Date(), targetProject);
		sourceProjectId = sourceProject.getId();
		issueIdMapping = new HashMap<>();
		for (var entry: issueMapping.entrySet())
			issueIdMapping.put(entry.getKey().getId(), entry.getValue().getId());
	}

	public Project getSourceProject() {
		return Cheeta.getInstance(Dao.class).load(Project.class, sourceProjectId);
	}

	public Project getTargetProject() {
		return getProject();
	}
	
	public Map<Long, Long> getIssueIdMapping() {
		return issueIdMapping;
	}

	@Override
	public String getActivity() {
		return "copied";
	}
	
}

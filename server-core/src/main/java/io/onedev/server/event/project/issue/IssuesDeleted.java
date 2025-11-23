package io.cheeta.server.event.project.issue;

import io.cheeta.server.event.project.ProjectEvent;
import io.cheeta.server.model.Issue;
import io.cheeta.server.model.Project;
import io.cheeta.server.security.SecurityUtils;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

public class IssuesDeleted extends ProjectEvent {
	
	private static final long serialVersionUID = 1L;
	
	private final Collection<Long> issueIds;
	
	public IssuesDeleted(Project project, Collection<Issue> issues) {
		super(SecurityUtils.getUser(), new Date(), project);
		issueIds = issues.stream().map(Issue::getId).collect(Collectors.toSet());
	}

	public Collection<Long> getIssueIds() {
		return issueIds;
	}

	@Override
	public String getActivity() {
		return "deleted";
	}
	
}

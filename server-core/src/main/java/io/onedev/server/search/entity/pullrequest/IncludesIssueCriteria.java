package io.cheeta.server.search.entity.pullrequest;

import java.util.Collection;
import java.util.HashSet;

import org.jspecify.annotations.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import org.eclipse.jgit.lib.ObjectId;

import io.cheeta.server.Cheeta;
import io.cheeta.server.model.Issue;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.search.entity.EntityQuery;
import io.cheeta.server.util.ProjectScope;
import io.cheeta.server.util.criteria.Criteria;
import io.cheeta.server.xodus.CommitInfoService;
import io.cheeta.server.xodus.PullRequestInfoService;

public class IncludesIssueCriteria extends Criteria<PullRequest> {

	private static final long serialVersionUID = 1L;

	private final Issue issue;
	
	private final String value;
	
	public IncludesIssueCriteria(@Nullable Project project, String value) {
		issue = EntityQuery.getIssue(project, value);
		this.value = value;
	}
	
	public IncludesIssueCriteria(Issue issue) {
		this.issue = issue;
		value = String.valueOf(issue.getNumber());
	}
	
	@Override
	public Predicate getPredicate(@Nullable ProjectScope projectScope, CriteriaQuery<?> query, From<PullRequest, PullRequest> from, 
			CriteriaBuilder builder) {
		Collection<Long> pullRequestIds = new HashSet<>();
		issue.getProject().getTree().stream().filter(it->it.isCodeManagement()).forEach(it-> {
			pullRequestIds.addAll(getPullRequestIds(it));
		});
		if (!pullRequestIds.isEmpty()) 
			return from.get(PullRequest.PROP_ID).in(pullRequestIds);
		else 
			return builder.disjunction();
	}
	
	private Collection<Long> getPullRequestIds(Project project) {
		Collection<Long> pullRequestIds = new HashSet<>();
		for (ObjectId commit: Cheeta.getInstance(CommitInfoService.class).getFixCommits(project.getId(), issue.getId(), false))
			pullRequestIds.addAll(Cheeta.getInstance(PullRequestInfoService.class).getPullRequestIds(project, commit));
		return pullRequestIds;
	}
	
	@Override
	public boolean matches(PullRequest request) {
		return getPullRequestIds(request.getProject()).contains(request.getId());
	}

	@Override
	public String toStringWithoutParens() {
		return PullRequestQuery.getRuleName(PullRequestQueryLexer.IncludesIssue) + " " + quote(value);
	}

}

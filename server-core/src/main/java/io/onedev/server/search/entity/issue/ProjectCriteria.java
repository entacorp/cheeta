package io.cheeta.server.search.entity.issue;

import org.jspecify.annotations.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;

import io.cheeta.commons.utils.match.WildcardUtils;
import io.cheeta.server.Cheeta;
import io.cheeta.server.service.ProjectService;
import io.cheeta.server.model.Issue;
import io.cheeta.server.model.Project;
import io.cheeta.server.util.ProjectScope;
import io.cheeta.server.util.criteria.Criteria;

public class ProjectCriteria extends Criteria<Issue> {

	private static final long serialVersionUID = 1L;
	
	private String projectPath;
	
	private final int operator;

	public ProjectCriteria(String projectPath, int operator) {
		this.projectPath = projectPath;
		this.operator = operator;
	}

	@Override
	public Predicate getPredicate(@Nullable ProjectScope projectScope, CriteriaQuery<?> query, From<Issue, Issue> from, CriteriaBuilder builder) {
		var predicate =  Cheeta.getInstance(ProjectService.class).getPathMatchPredicate(
				builder, from.join(Issue.PROP_PROJECT, JoinType.INNER), projectPath);
		if (operator == IssueQueryLexer.IsNot)
			predicate = builder.not(predicate);
		return predicate;
	}

	@Override
	public boolean matches(Issue issue) {
		var matches = WildcardUtils.matchPath(projectPath, issue.getProject().getPath());
		if (operator == IssueQueryLexer.IsNot)
			matches = !matches;
		return matches;
	}

	@Override
	public void onMoveProject(String oldPath, String newPath) {
		projectPath = Project.substitutePath(projectPath, oldPath, newPath);
	}

	@Override
	public boolean isUsingProject(String projectPath) {
		return Project.containsPath(this.projectPath, projectPath);
	}

	@Override
	public String toStringWithoutParens() {
		return quote(Issue.NAME_PROJECT) + " " 
				+ IssueQuery.getRuleName(operator) + " " 
				+ quote(projectPath);
	}

}

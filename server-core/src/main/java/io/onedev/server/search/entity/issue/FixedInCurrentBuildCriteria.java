package io.cheeta.server.search.entity.issue;

import org.jspecify.annotations.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import io.cheeta.commons.utils.ExplicitException;
import io.cheeta.server.model.Build;
import io.cheeta.server.model.Issue;
import io.cheeta.server.util.ProjectScope;
import io.cheeta.server.util.criteria.Criteria;

public class FixedInCurrentBuildCriteria extends Criteria<Issue> {

	private static final long serialVersionUID = 1L;
	
	@Override
	public Predicate getPredicate(@Nullable ProjectScope projectScope, CriteriaQuery<?> query, From<Issue, Issue> from, CriteriaBuilder builder) {
		if (Build.get() != null)
			return new FixedInBuildCriteria(Build.get()).getPredicate(projectScope, query, from, builder);
		else
			throw new ExplicitException("No build in query context");
	}

	@Override
	public boolean matches(Issue issue) {
		if (Build.get() != null)
			return new FixedInBuildCriteria(Build.get()).matches(issue);
		else
			throw new ExplicitException("No build in query context");
	}

	@Override
	public String toStringWithoutParens() {
		return IssueQuery.getRuleName(IssueQueryLexer.FixedInCurrentBuild);
	}

}

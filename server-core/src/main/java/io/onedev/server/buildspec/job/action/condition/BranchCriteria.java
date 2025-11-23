package io.cheeta.server.buildspec.job.action.condition;

import static io.cheeta.server.buildspec.job.action.condition.ActionCondition.getRuleName;
import static io.cheeta.server.model.Build.NAME_BRANCH;

import org.jspecify.annotations.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import io.cheeta.commons.utils.match.PathMatcher;
import io.cheeta.server.model.Build;
import io.cheeta.server.util.ProjectScope;
import io.cheeta.server.util.criteria.Criteria;

public class BranchCriteria extends Criteria<Build> {

	private static final long serialVersionUID = 1L;
	
	private final String branch;
	
	private final int operator;
	
	public BranchCriteria(String branch, int operator) {
		this.branch = branch;
		this.operator = operator;
	}
	
	@Override
	public Predicate getPredicate(@Nullable ProjectScope projectScope, CriteriaQuery<?> query, From<Build, Build> from, CriteriaBuilder builder) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public boolean matches(Build build) {
		var matches = build.getBranch() != null && new PathMatcher().matches(branch, build.getBranch());
		if (operator == ActionConditionLexer.IsNot)
			matches = !matches;
		return matches;
	}

	@Override
	public String toStringWithoutParens() {
		return quote(NAME_BRANCH) + " " + getRuleName(operator) + " " + quote(branch);
	}
	
}

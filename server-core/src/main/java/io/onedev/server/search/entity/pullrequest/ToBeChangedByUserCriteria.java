package io.cheeta.server.search.entity.pullrequest;

import org.jspecify.annotations.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.User;
import io.cheeta.server.util.ProjectScope;
import io.cheeta.server.util.criteria.AndCriteria;
import io.cheeta.server.util.criteria.Criteria;

public class ToBeChangedByUserCriteria extends Criteria<PullRequest> {

	private static final long serialVersionUID = 1L;

	private final User user;
	
	public ToBeChangedByUserCriteria(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}

	@Override
	public Predicate getPredicate(@Nullable ProjectScope projectScope, CriteriaQuery<?> query, From<PullRequest, PullRequest> from, CriteriaBuilder builder) {
		return getCriteria(user).getPredicate(projectScope, query, from, builder);
	}

	@Override
	public boolean matches(PullRequest request) {
		return getCriteria(user).matches(request);
	}

	@SuppressWarnings("unchecked")
	private Criteria<PullRequest> getCriteria(User user) {
		return new AndCriteria<>(
				new OpenCriteria(),
				new SubmittedByUserCriteria(user), 
				new SomeoneRequestedForChangesCriteria());
	}
	
	@Override
	public String toStringWithoutParens() {
		return PullRequestQuery.getRuleName(PullRequestQueryLexer.ToBeChangedBy) + " " + quote(user.getName());
	}

}

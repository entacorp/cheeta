package io.cheeta.server.search.entity.issue;

import static io.cheeta.server.web.translation.Translation._T;

import org.jspecify.annotations.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import io.cheeta.commons.utils.ExplicitException;
import io.cheeta.server.model.Issue;
import io.cheeta.server.model.IssueComment;
import io.cheeta.server.model.User;
import io.cheeta.server.util.ProjectScope;
import io.cheeta.server.util.criteria.Criteria;

public class CommentedByMeCriteria extends Criteria<Issue> {

	private static final long serialVersionUID = 1L;

	@Override
	public Predicate getPredicate(@Nullable ProjectScope projectScope, CriteriaQuery<?> query, From<Issue, Issue> from, CriteriaBuilder builder) {
		if (User.get() != null) {
			Subquery<IssueComment> commentQuery = query.subquery(IssueComment.class);
			Root<IssueComment> comment = commentQuery.from(IssueComment.class);
			commentQuery.select(comment);
			commentQuery.where(builder.and(
					builder.equal(comment.get(IssueComment.PROP_ISSUE), from),
					builder.equal(comment.get(IssueComment.PROP_USER), User.get())));
			return builder.exists(commentQuery);
		} else {
			throw new ExplicitException(_T("Please login to perform this query"));
		}
	}

	@Override
	public boolean matches(Issue issue) {
		if (User.get() != null)
			return issue.getComments().stream().anyMatch(it->it.getUser().equals(User.get()));
		else
			throw new ExplicitException(_T("Please login to perform this query"));
	}

	@Override
	public String toStringWithoutParens() {
		return IssueQuery.getRuleName(IssueQueryLexer.CommentedByMe);
	}

}

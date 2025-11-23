package io.cheeta.server.search.entity.pack;

import static io.cheeta.server.web.translation.Translation._T;

import org.jspecify.annotations.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import io.cheeta.commons.utils.ExplicitException;
import io.cheeta.server.model.Pack;
import io.cheeta.server.model.User;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.util.ProjectScope;

public class PublishedByMeCriteria extends PublishedByCriteria {

	private static final long serialVersionUID = 1L;

	@Override
	public Predicate getPredicate(@Nullable ProjectScope projectScope, CriteriaQuery<?> query, From<Pack, Pack> from, CriteriaBuilder builder) {
		if (User.get() != null) {
			Path<User> attribute = from.get(Pack.PROP_USER);
			return builder.equal(attribute, User.get());
		} else {
			throw new ExplicitException(_T("Please login to perform this query"));
		}
	}

	@Override
	public User getUser() {
		return SecurityUtils.getUser();
	}

	@Override
	public boolean matches(Pack pack) {
		if (User.get() != null)
			return User.get().equals(pack.getUser());
		else
			throw new ExplicitException(_T("Please login to perform this query"));
	}

	@Override
	public String toStringWithoutParens() {
		return PackQuery.getRuleName(PackQueryLexer.PublishedByMe);
	}

}

package io.cheeta.server.search.entity.project;

import org.jspecify.annotations.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import io.cheeta.commons.utils.match.WildcardUtils;
import io.cheeta.server.Cheeta;
import io.cheeta.server.service.ProjectService;
import io.cheeta.server.model.Project;
import io.cheeta.server.util.ProjectScope;
import io.cheeta.server.util.criteria.Criteria;

public class PathCriteria extends Criteria<Project> {

	private static final long serialVersionUID = 1L;

	private final String value;
	
	private final int operator;
	
	public PathCriteria(String value, int operator) {
		this.value = value;
		this.operator = operator;
	}

	@Override
	public Predicate getPredicate(@Nullable ProjectScope projectScope, CriteriaQuery<?> query, From<Project, Project> from, CriteriaBuilder builder) {
		var predicate = Cheeta.getInstance(ProjectService.class).getPathMatchPredicate(builder, from, value);
		if (operator == ProjectQueryLexer.IsNot)
			predicate = builder.not(predicate);
		return predicate;
	}

	@Override
	public boolean matches(Project project) {
		var matches = WildcardUtils.matchPath(value.toLowerCase(), project.getPath().toLowerCase());
		if (operator == ProjectQueryLexer.IsNot)
			matches = !matches;
		return matches;
	}

	@Override
	public String toStringWithoutParens() {
		return Criteria.quote(Project.NAME_PATH) + " " 
				+ ProjectQuery.getRuleName(operator) + " " 
				+ Criteria.quote(value);
	}

}

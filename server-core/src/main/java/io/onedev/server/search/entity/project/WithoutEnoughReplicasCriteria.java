package io.cheeta.server.search.entity.project;

import org.jspecify.annotations.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.ProjectService;
import io.cheeta.server.model.Project;
import io.cheeta.server.util.ProjectScope;
import io.cheeta.server.util.criteria.Criteria;

public class WithoutEnoughReplicasCriteria extends Criteria<Project> {

	private static final long serialVersionUID = 1L;

	@Override
	public Predicate getPredicate(@Nullable ProjectScope projectScope, CriteriaQuery<?> query, From<Project, Project> from, CriteriaBuilder builder) {
		ProjectService projectService = Cheeta.getInstance(ProjectService.class);
		return forManyValues(builder, from.get(Project.PROP_ID),
				projectService.getIdsWithoutEnoughReplicas(), projectService.getIds());
	}

	@Override
	public boolean matches(Project project) {
		return Cheeta.getInstance(ProjectService.class).isWithoutEnoughReplicas(project.getId());
	}

	@Override
	public String toStringWithoutParens() {
		return ProjectQuery.getRuleName(ProjectQueryLexer.WithoutEnoughReplicas);
	}

}

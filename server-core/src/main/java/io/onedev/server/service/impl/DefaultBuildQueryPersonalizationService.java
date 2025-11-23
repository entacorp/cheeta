package io.cheeta.server.service.impl;

import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import org.hibernate.criterion.Restrictions;

import io.cheeta.server.model.BuildQueryPersonalization;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.User;
import io.cheeta.server.model.support.NamedQuery;
import io.cheeta.server.persistence.annotation.Sessional;
import io.cheeta.server.persistence.annotation.Transactional;
import io.cheeta.server.persistence.dao.EntityCriteria;
import io.cheeta.server.service.BuildQueryPersonalizationService;

@Singleton
public class DefaultBuildQueryPersonalizationService extends BaseEntityService<BuildQueryPersonalization>
		implements BuildQueryPersonalizationService {

	@Sessional
	@Override
	public BuildQueryPersonalization find(Project project, User user) {
		EntityCriteria<BuildQueryPersonalization> criteria = newCriteria();
		criteria.add(Restrictions.and(Restrictions.eq("project", project), Restrictions.eq("user", user)));
		criteria.setCacheable(true);
		return find(criteria);
	}

	@Transactional
	@Override
	public void createOrUpdate(BuildQueryPersonalization personalization) {
		Collection<String> retainNames = new HashSet<>();
		retainNames.addAll(personalization.getQueries().stream()
				.map(it->NamedQuery.PERSONAL_NAME_PREFIX+it.getName()).collect(Collectors.toSet()));
		retainNames.addAll(personalization.getProject().getNamedBuildQueries().stream()
				.map(it->NamedQuery.COMMON_NAME_PREFIX+it.getName()).collect(Collectors.toSet()));
		personalization.getQuerySubscriptionSupport().getQuerySubscriptions().retainAll(retainNames);
		
		if (personalization.getQuerySubscriptionSupport().getQuerySubscriptions().isEmpty() && personalization.getQueries().isEmpty()) {
			if (!personalization.isNew())
				delete(personalization);
		} else {
			dao.persist(personalization);
		}
	}

}

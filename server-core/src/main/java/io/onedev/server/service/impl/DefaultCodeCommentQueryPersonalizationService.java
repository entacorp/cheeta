package io.cheeta.server.service.impl;

import javax.inject.Singleton;

import org.hibernate.criterion.Restrictions;

import io.cheeta.server.model.CodeCommentQueryPersonalization;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.User;
import io.cheeta.server.persistence.annotation.Sessional;
import io.cheeta.server.persistence.annotation.Transactional;
import io.cheeta.server.persistence.dao.EntityCriteria;
import io.cheeta.server.service.CodeCommentQueryPersonalizationService;

@Singleton
public class DefaultCodeCommentQueryPersonalizationService extends BaseEntityService<CodeCommentQueryPersonalization>
		implements CodeCommentQueryPersonalizationService {

	@Sessional
	@Override
	public CodeCommentQueryPersonalization find(Project project, User user) {
		EntityCriteria<CodeCommentQueryPersonalization> criteria = newCriteria();
		criteria.add(Restrictions.and(Restrictions.eq("project", project), Restrictions.eq("user", user)));
		criteria.setCacheable(true);
		return find(criteria);
	}

	@Transactional
	@Override
	public void createOrUpdate(CodeCommentQueryPersonalization personalization) {
		if (personalization.getQueries().isEmpty()) {
			if (!personalization.isNew())
				delete(personalization);
		} else {
			dao.persist(personalization);
		}
	}
	
}

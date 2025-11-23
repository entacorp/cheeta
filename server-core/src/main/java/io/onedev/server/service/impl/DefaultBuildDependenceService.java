package io.cheeta.server.service.impl;

import javax.inject.Singleton;

import com.google.common.base.Preconditions;

import io.cheeta.server.model.BuildDependence;
import io.cheeta.server.persistence.annotation.Transactional;
import io.cheeta.server.service.BuildDependenceService;

@Singleton
public class DefaultBuildDependenceService extends BaseEntityService<BuildDependence> implements BuildDependenceService {

	@Transactional
	@Override
	public void create(BuildDependence dependence) {
		Preconditions.checkState(dependence.isNew());
		dao.persist(dependence);
	}
	
}

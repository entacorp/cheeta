package io.cheeta.server.service.impl;

import java.util.List;

import javax.inject.Singleton;

import io.cheeta.server.model.DashboardVisit;
import io.cheeta.server.persistence.annotation.Transactional;
import io.cheeta.server.service.DashboardVisitService;

@Singleton
public class DefaultDashboardVisitService extends BaseEntityService<DashboardVisit>
		implements DashboardVisitService {

	@Override
	public List<DashboardVisit> query() {
		return query(true);
	}

	@Override
	public int count() {
		return count(true);
	}
	
	@Transactional
	@Override
	public void createOrUpdate(DashboardVisit visit) {
		dao.persist(visit);
	}
	
}

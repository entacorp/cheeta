package io.cheeta.server.service;

import io.cheeta.server.model.DashboardVisit;

public interface DashboardVisitService extends EntityService<DashboardVisit> {

	void createOrUpdate(DashboardVisit visit);

}
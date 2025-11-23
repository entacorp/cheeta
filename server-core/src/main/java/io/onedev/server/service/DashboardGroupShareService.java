package io.cheeta.server.service;

import io.cheeta.server.model.Dashboard;
import io.cheeta.server.model.DashboardGroupShare;

import java.util.Collection;

public interface DashboardGroupShareService extends EntityService<DashboardGroupShare> {

	void syncShares(Dashboard dashboard, Collection<String> groupNames);

}

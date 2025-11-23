package io.cheeta.server.service;

import io.cheeta.server.model.Dashboard;
import io.cheeta.server.model.DashboardUserShare;

import java.util.Collection;

public interface DashboardUserShareService extends EntityService<DashboardUserShare> {

	void syncShares(Dashboard dashboard, Collection<String> userNames);

}
package io.cheeta.server.service;

import io.cheeta.server.model.BuildDependence;

public interface BuildDependenceService extends EntityService<BuildDependence> {
	
	void create(BuildDependence dependence);
	
}

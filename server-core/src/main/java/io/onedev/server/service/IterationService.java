package io.cheeta.server.service;

import io.cheeta.server.model.Iteration;
import io.cheeta.server.model.Project;

import org.jspecify.annotations.Nullable;

public interface IterationService extends EntityService<Iteration> {
	
	@Nullable
    Iteration findInHierarchy(Project project, String name);
	
	void delete(Iteration iteration);

	Iteration findInHierarchy(String iterationFQN);

    void createOrUpdate(Iteration iteration);
	
}
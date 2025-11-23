package io.cheeta.server.service;

import io.cheeta.server.model.Issue;
import io.cheeta.server.model.Stopwatch;
import io.cheeta.server.model.User;

import org.jspecify.annotations.Nullable;

public interface StopwatchService extends EntityService<Stopwatch> {
	
	@Nullable
	Stopwatch find(User user, Issue issue);
	
	Stopwatch startWork(User user, Issue issue);
	
	void stopWork(Stopwatch stopwatch);
	
}

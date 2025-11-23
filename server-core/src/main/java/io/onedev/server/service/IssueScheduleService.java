package io.cheeta.server.service;

import java.util.Collection;

import io.cheeta.server.model.Issue;
import io.cheeta.server.model.IssueSchedule;
import io.cheeta.server.model.Iteration;

public interface IssueScheduleService extends EntityService<IssueSchedule> {
	
 	void syncIterations(Issue issue, Collection<Iteration> iterations);

    void create(IssueSchedule schedule);

    void populateSchedules(Collection<Issue> issues);
	
}
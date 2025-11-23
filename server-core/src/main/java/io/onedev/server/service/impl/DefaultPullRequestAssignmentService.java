package io.cheeta.server.service.impl;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.base.Preconditions;
import io.cheeta.server.service.PullRequestAssignmentService;
import io.cheeta.server.event.ListenerRegistry;
import io.cheeta.server.event.project.pullrequest.PullRequestAssigned;
import io.cheeta.server.event.project.pullrequest.PullRequestUnassigned;
import io.cheeta.server.model.PullRequestAssignment;
import io.cheeta.server.persistence.annotation.Transactional;
import io.cheeta.server.security.SecurityUtils;

@Singleton
public class DefaultPullRequestAssignmentService extends BaseEntityService<PullRequestAssignment>
		implements PullRequestAssignmentService {

	@Inject
	private ListenerRegistry listenerRegistry;

	@Transactional
	@Override
	public void create(PullRequestAssignment assignment) {
		Preconditions.checkState(assignment.isNew());
		dao.persist(assignment);

		listenerRegistry.post(new PullRequestAssigned(
				SecurityUtils.getUser(), new Date(), 
				assignment.getRequest(), assignment.getUser()));
	}

	@Transactional
	@Override
	public void delete(PullRequestAssignment assignment) {
		super.delete(assignment);
		
		listenerRegistry.post(new PullRequestUnassigned(
				SecurityUtils.getUser(), new Date(), 
				assignment.getRequest(), assignment.getUser()));
	}
		
}

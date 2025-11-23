package io.cheeta.server.service.impl;

import static io.cheeta.server.model.PullRequestTouch.PROP_REQUEST_ID;
import static java.lang.String.format;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.hibernate.criterion.Restrictions;

import io.cheeta.server.event.Listen;
import io.cheeta.server.event.ListenerRegistry;
import io.cheeta.server.event.entity.EntityRemoved;
import io.cheeta.server.event.project.pullrequest.PullRequestChanged;
import io.cheeta.server.event.project.pullrequest.PullRequestCommentCreated;
import io.cheeta.server.event.project.pullrequest.PullRequestCommentEdited;
import io.cheeta.server.event.project.pullrequest.PullRequestOpened;
import io.cheeta.server.event.project.pullrequest.PullRequestTouched;
import io.cheeta.server.model.AbstractEntity;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.PullRequestComment;
import io.cheeta.server.model.PullRequestTouch;
import io.cheeta.server.persistence.TransactionService;
import io.cheeta.server.persistence.annotation.Sessional;
import io.cheeta.server.persistence.annotation.Transactional;
import io.cheeta.server.persistence.dao.EntityCriteria;
import io.cheeta.server.service.ProjectService;
import io.cheeta.server.service.PullRequestTouchService;

@Singleton
public class DefaultPullRequestTouchService extends BaseEntityService<PullRequestTouch>
		implements PullRequestTouchService {

	@Inject
	private ProjectService projectService;

	@Inject
	private TransactionService transactionService;

	@Inject
    private ListenerRegistry listenerRegistry;

	@Transactional
	@Override
	public void touch(Project project, Long requestId, boolean newRequest) {
		var projectId = project.getId();
		transactionService.runAfterCommit(() -> transactionService.runAsync(() -> {
            var innerProject = projectService.load(projectId);
            if (!newRequest) {
                var query = getSession().createQuery(format("delete from PullRequestTouch where project=:project and %s=:%s", PROP_REQUEST_ID, PROP_REQUEST_ID));
                query.setParameter("project", innerProject);
                query.setParameter(PROP_REQUEST_ID, requestId);
                query.executeUpdate();
            }

			var touch = new PullRequestTouch();
			touch.setProject(innerProject);
			touch.setRequestId(requestId);
			dao.persist(touch);

			listenerRegistry.post(new PullRequestTouched(innerProject, requestId));
		}));
	}

	@Sessional
	@Override
	public List<PullRequestTouch> queryTouchesAfter(Long projectId, Long afterTouchId, int count) {
		EntityCriteria<PullRequestTouch> criteria = EntityCriteria.of(PullRequestTouch.class);
		criteria.add(Restrictions.eq("project.id", projectId));
		criteria.add(Restrictions.gt(AbstractEntity.PROP_ID, afterTouchId));
		return dao.query(criteria, 0, count);
	}
	
	@Transactional
	@Listen
	public void on(PullRequestOpened event) {
		touch(event.getProject(), event.getRequest().getId(), true);
	}

	@Transactional
	@Listen
	public void on(PullRequestCommentCreated event) {
		touch(event.getProject(), event.getRequest().getId(), false);
	}

	@Transactional
	@Listen
	public void on(PullRequestCommentEdited event) {
		touch(event.getProject(), event.getRequest().getId(), false);
	}

	@Transactional
	@Listen
	public void on(PullRequestChanged event) {
		touch(event.getProject(), event.getRequest().getId(), false);
	}

	@Transactional
	@Listen
	public void on(EntityRemoved event) {
		if (event.getEntity() instanceof PullRequest) {
			PullRequest request = (PullRequest) event.getEntity();
			touch(request.getProject(), request.getId(), false);
		} else if (event.getEntity() instanceof PullRequestComment) {
			PullRequestComment comment = (PullRequestComment) event.getEntity();
			touch(comment.getRequest().getProject(), comment.getRequest().getId(), false);
		}
	}
	
}
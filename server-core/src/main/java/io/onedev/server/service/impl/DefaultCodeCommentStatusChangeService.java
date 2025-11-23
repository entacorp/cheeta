package io.cheeta.server.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Predicate;

import com.google.common.base.Preconditions;

import io.cheeta.server.event.ListenerRegistry;
import io.cheeta.server.event.project.codecomment.CodeCommentStatusChanged;
import io.cheeta.server.event.project.pullrequest.PullRequestCodeCommentStatusChanged;
import io.cheeta.server.model.CodeComment;
import io.cheeta.server.model.CodeCommentReply;
import io.cheeta.server.model.CodeCommentStatusChange;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.User;
import io.cheeta.server.persistence.annotation.Sessional;
import io.cheeta.server.persistence.annotation.Transactional;
import io.cheeta.server.service.CodeCommentStatusChangeService;

@Singleton
public class DefaultCodeCommentStatusChangeService extends BaseEntityService<CodeCommentStatusChange>
		implements CodeCommentStatusChangeService {

	@Inject
	private ListenerRegistry listenerRegistry;

	@Transactional
	@Override
	public void create(CodeCommentStatusChange change, String note) {
		Preconditions.checkState(change.isNew());
		
		CodeComment comment = change.getComment();
		comment.setResolved(change.isResolved());
		
		dao.persist(change);
		
		if (note != null) {
			CodeCommentReply reply = new CodeCommentReply();
			reply.setComment(comment);
			reply.setCompareContext(change.getCompareContext());
			reply.setContent(note);
			reply.setDate(change.getDate());
			reply.setUser(change.getUser());
			dao.persist(reply);
			
			comment.setReplyCount(comment.getReplyCount()+1);
		}
		listenerRegistry.post(new CodeCommentStatusChanged(change, note));
		
		PullRequest request = comment.getCompareContext().getPullRequest();
		if (request != null) 
			listenerRegistry.post(new PullRequestCodeCommentStatusChanged(request, change, note));
	}
	
	@Transactional
	@Override
	public void create(Collection<CodeCommentStatusChange> changes, String note) {
		for (CodeCommentStatusChange  change: changes)
			create(change, note);
	}

	@Sessional
	@Override
	public List<CodeCommentStatusChange> query(User creator, Date fromDate, Date toDate) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<CodeCommentStatusChange> query = builder.createQuery(CodeCommentStatusChange.class);
		From<CodeCommentStatusChange, CodeCommentStatusChange> root = query.from(CodeCommentStatusChange.class);
		
		List<Predicate> predicates = new ArrayList<>();

		predicates.add(builder.equal(root.get(CodeCommentStatusChange.PROP_USER), creator));
		predicates.add(builder.greaterThanOrEqualTo(root.get(CodeCommentStatusChange.PROP_DATE), fromDate));
		predicates.add(builder.lessThanOrEqualTo(root.get(CodeCommentStatusChange.PROP_DATE), toDate));
			
		query.where(predicates.toArray(new Predicate[0]));
		
		return getSession().createQuery(query).getResultList();
	}
	
}

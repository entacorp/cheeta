package io.cheeta.server.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.google.common.base.Preconditions;
import org.eclipse.jgit.lib.ObjectId;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.CodeCommentStatusChangeService;
import io.cheeta.server.service.PendingSuggestionApplyService;
import io.cheeta.server.git.BlobEdits;
import io.cheeta.server.git.GitUtils;
import io.cheeta.server.git.service.GitService;
import io.cheeta.server.model.CodeComment;
import io.cheeta.server.model.CodeCommentStatusChange;
import io.cheeta.server.model.PendingSuggestionApply;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.User;
import io.cheeta.server.model.support.CompareContext;
import io.cheeta.server.persistence.annotation.Sessional;
import io.cheeta.server.persistence.annotation.Transactional;
import io.cheeta.server.security.SecurityUtils;

@Singleton
public class DefaultPendingSuggestionApplyService extends BaseEntityService<PendingSuggestionApply>
		implements PendingSuggestionApplyService {

	@Inject
	private GitService gitService;

	@Transactional
	@Override
	public ObjectId apply(User user, PullRequest request, String commitMessage) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<PendingSuggestionApply> criteriaQuery = 
				builder.createQuery(PendingSuggestionApply.class);
		Root<PendingSuggestionApply> root = criteriaQuery.from(PendingSuggestionApply.class);

		criteriaQuery.where(builder.and(
				builder.equal(root.get(PendingSuggestionApply.PROP_REQUEST), request), 
				builder.equal(root.get(PendingSuggestionApply.PROP_USER), user)));
		
		ObjectId headCommitId = request.getLatestUpdate().getHeadCommit();
		
		BlobEdits blobEdits = new BlobEdits();
		
		List<CodeComment> unresolvedComments = new ArrayList<>();
		for (PendingSuggestionApply pendingApply: getSession().createQuery(criteriaQuery).list()) {
			CodeComment comment = pendingApply.getComment();
			unresolvedComments.add(comment);
			blobEdits.applySuggestion(request.getSourceProject(), comment.getMark(), 
					pendingApply.getSuggestion(), headCommitId); 
			delete(pendingApply);
		}
		
		String refName = GitUtils.branch2ref(request.getSourceBranch());
		
		ObjectId newCommitId = gitService.commit(request.getSourceProject(), 
				blobEdits, refName, headCommitId, headCommitId, user.asPerson(), 
				commitMessage, false);

		for (CodeComment comment: unresolvedComments) {
			CodeCommentStatusChange change = new CodeCommentStatusChange();
			change.setComment(comment);
			change.setResolved(true);
			change.setUser(SecurityUtils.getUser());
			CompareContext compareContext = new CompareContext();
			compareContext.setPullRequest(request);
			compareContext.setOldCommitHash(comment.getMark().getCommitHash());
			compareContext.setNewCommitHash(newCommitId.name());
			change.setCompareContext(compareContext);
			Cheeta.getInstance(CodeCommentStatusChangeService.class).create(change, "Suggestion applied");
		}

		return newCommitId;
	}

	@Transactional
	@Override
	public void discard(User user, PullRequest request) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaDelete<PendingSuggestionApply> criteriaDelete = builder.createCriteriaDelete(PendingSuggestionApply.class);
		Root<PendingSuggestionApply> root = criteriaDelete.from(PendingSuggestionApply.class);

		if (user != null) {
			criteriaDelete.where(builder.and(
					builder.equal(root.get(PendingSuggestionApply.PROP_REQUEST), request), 
					builder.equal(root.get(PendingSuggestionApply.PROP_USER), user)));
		} else {
			criteriaDelete.where(builder.equal(root.get(PendingSuggestionApply.PROP_REQUEST), request));
		}

		getSession().createQuery(criteriaDelete).executeUpdate();
	}

	@Sessional
	@Override
	public List<PendingSuggestionApply> query(User user, PullRequest request) {
		CriteriaBuilder builder = getSession().getCriteriaBuilder();
		CriteriaQuery<PendingSuggestionApply> criteriaQuery = builder.createQuery(PendingSuggestionApply.class);
		Root<PendingSuggestionApply> root = criteriaQuery.from(PendingSuggestionApply.class);

		criteriaQuery.where(builder.and(
				builder.equal(root.get(PendingSuggestionApply.PROP_REQUEST), request), 
				builder.equal(root.get(PendingSuggestionApply.PROP_USER), user)));

		criteriaQuery.select(root);
		
		return getSession().createQuery(criteriaQuery).list();
	}

	@Transactional
	@Override
	public void create(PendingSuggestionApply pendingSuggestionApply) {
		Preconditions.checkState(pendingSuggestionApply.isNew());
		dao.persist(pendingSuggestionApply);
	}

}

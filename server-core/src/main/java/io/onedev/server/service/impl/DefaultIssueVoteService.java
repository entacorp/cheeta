package io.cheeta.server.service.impl;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.base.Preconditions;
import io.cheeta.server.service.IssueVoteService;
import io.cheeta.server.service.IssueWatchService;
import io.cheeta.server.model.IssueVote;
import io.cheeta.server.persistence.annotation.Transactional;

@Singleton
public class DefaultIssueVoteService extends BaseEntityService<IssueVote>
		implements IssueVoteService {

	@Inject
	private IssueWatchService watchService;

	@Transactional
	@Override
	public void create(IssueVote vote) {
		Preconditions.checkState(vote.isNew());
		vote.getIssue().setVoteCount(vote.getIssue().getVoteCount()+1);
		dao.persist(vote);
		watchService.watch(vote.getIssue(), vote.getUser(), true);
	}

	@Transactional
	@Override
	public void delete(IssueVote vote) {
		super.delete(vote);
		vote.getIssue().setVoteCount(vote.getIssue().getVoteCount()-1);
	}

}

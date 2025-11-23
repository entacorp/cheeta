package io.cheeta.server.service;

import io.cheeta.server.model.IssueVote;

public interface IssueVoteService extends EntityService<IssueVote> {

    void create(IssueVote vote);
	
}

package io.cheeta.server.service;

import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.PullRequestMention;
import io.cheeta.server.model.User;

public interface PullRequestMentionService extends EntityService<PullRequestMention> {

	void mention(PullRequest request, User user);

}

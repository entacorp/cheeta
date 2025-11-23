package io.cheeta.server.service.impl;

import javax.inject.Singleton;

import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.PullRequestMention;
import io.cheeta.server.model.User;
import io.cheeta.server.service.PullRequestMentionService;

@Singleton
public class DefaultPullRequestMentionService extends BaseEntityService<PullRequestMention>
		implements PullRequestMentionService {

	@Override
	public void mention(PullRequest request, User user) {
		if (request.getMentions().stream().noneMatch(it->it.getUser().equals(user))) {
			PullRequestMention mention = new PullRequestMention();
			mention.setRequest(request);
			mention.setUser(user);
			dao.persist(mention);
		}
	}

}

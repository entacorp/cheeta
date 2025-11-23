package io.cheeta.server.service.impl;

import javax.inject.Singleton;

import io.cheeta.server.model.Issue;
import io.cheeta.server.model.IssueMention;
import io.cheeta.server.model.User;
import io.cheeta.server.service.IssueMentionService;

@Singleton
public class DefaultIssueMentionService extends BaseEntityService<IssueMention>
		implements IssueMentionService {

	@Override
	public void mention(Issue issue, User user) {
		if (issue.getMentions().stream().noneMatch(it->it.getUser().equals(user))) {
			IssueMention mention = new IssueMention();
			mention.setIssue(issue);
			mention.setUser(user);
			dao.persist(mention);
		}
	}

}

package io.cheeta.server.service;

import io.cheeta.server.model.Issue;
import io.cheeta.server.model.IssueMention;
import io.cheeta.server.model.User;

public interface IssueMentionService extends EntityService<IssueMention> {

	void mention(Issue issue, User user);

}

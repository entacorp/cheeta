package io.cheeta.server.service;

import io.cheeta.server.model.Issue;
import io.cheeta.server.model.IssueAuthorization;
import io.cheeta.server.model.User;

public interface IssueAuthorizationService extends EntityService<IssueAuthorization> {

	void authorize(Issue issue, User user);

    void createOrUpdate(IssueAuthorization authorization);
	
}
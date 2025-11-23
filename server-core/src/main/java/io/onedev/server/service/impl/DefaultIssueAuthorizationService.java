package io.cheeta.server.service.impl;

import java.util.List;

import javax.inject.Singleton;

import io.cheeta.server.model.Issue;
import io.cheeta.server.model.IssueAuthorization;
import io.cheeta.server.model.User;
import io.cheeta.server.persistence.annotation.Transactional;
import io.cheeta.server.service.IssueAuthorizationService;

@Singleton
public class DefaultIssueAuthorizationService extends BaseEntityService<IssueAuthorization>
		implements IssueAuthorizationService {

	@Override
	public List<IssueAuthorization> query() {
		return query(true);
	}

	@Override
	public int count() {
		return count(true);
	}

	@Transactional
	@Override
	public void authorize(Issue issue, User user) {
		boolean authorized = false;
		for (IssueAuthorization authorization: issue.getAuthorizations()) {
			if (authorization.getUser().equals(user)) {
				authorized = true;
				break;
			}
		}
		if (!authorized) {
			IssueAuthorization authorization = new IssueAuthorization();
			authorization.setIssue(issue);
			authorization.setUser(user);
			issue.getAuthorizations().add(authorization);
			createOrUpdate(authorization);
		}
	}

	@Transactional
	@Override
	public void createOrUpdate(IssueAuthorization authorization) {
		dao.persist(authorization);
	}
	
}

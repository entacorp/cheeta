package io.cheeta.server.search.entity.issue;

import io.cheeta.server.model.Issue;
import io.cheeta.server.model.User;
import io.cheeta.server.util.criteria.Criteria;

public abstract class SubmittedByCriteria extends Criteria<Issue> {

	public abstract User getUser();
	
}

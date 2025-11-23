package io.cheeta.server.search.entity.pullrequest;

import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.PullRequest.Status;
import io.cheeta.server.util.criteria.Criteria;

public abstract class StatusCriteria extends Criteria<PullRequest> {

	public abstract Status getStatus();
}

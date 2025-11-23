package io.cheeta.server.search.entity.pullrequest;

import org.jspecify.annotations.Nullable;

import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.User;
import io.cheeta.server.util.criteria.Criteria;

public abstract class SubmittedByCriteria extends Criteria<PullRequest> {

    @Nullable
    public abstract User getUser();
    
}

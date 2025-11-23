package io.cheeta.server.service;

import java.util.Collection;

import org.jspecify.annotations.Nullable;

import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.PullRequestReview;
import io.cheeta.server.model.User;

public interface PullRequestReviewService extends EntityService<PullRequestReview> {
	
    void review(User user, PullRequest request, boolean approved, @Nullable String note);
	
	void populateReviews(Collection<PullRequest> requests);

    void createOrUpdate(User user, PullRequestReview review);
	
}

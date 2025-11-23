package io.cheeta.server.web.component.pullrequest.review;

import java.util.ArrayList;
import java.util.List;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.UserService;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.PullRequestReview;
import io.cheeta.server.model.User;
import io.cheeta.server.util.Similarities;
import io.cheeta.server.util.facade.UserCache;
import io.cheeta.server.web.WebConstants;
import io.cheeta.server.web.component.select2.Response;
import io.cheeta.server.web.component.select2.ResponseFiller;
import io.cheeta.server.web.component.user.choice.AbstractUserChoiceProvider;

public abstract class ReviewerProvider extends AbstractUserChoiceProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public void query(String term, int page, Response<User> response) {
		PullRequest request = getPullRequest();
		
		UserCache cache = Cheeta.getInstance(UserService.class).cloneCache();
		List<User> users = new ArrayList<>(cache.getUsers());
		users.sort(cache.comparingDisplayName(request.getParticipants()));
		
		for (PullRequestReview review: request.getReviews()) {
			if (review.getStatus() != PullRequestReview.Status.EXCLUDED)
				users.remove(review.getUser());
		}
		users.remove(request.getSubmitter());
		
		new ResponseFiller<User>(response).fill(new Similarities<User>(users) {

			private static final long serialVersionUID = 1L;

			@Override
			public double getSimilarScore(User object) {
				return cache.getSimilarScore(object, term); 
			}
			
		}, page, WebConstants.PAGE_SIZE);
	}

	protected abstract PullRequest getPullRequest();
	
}
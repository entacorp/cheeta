package io.cheeta.server.web.component.pullrequest.assignment;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.UserService;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.User;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.security.permission.WriteCode;
import io.cheeta.server.util.Similarities;
import io.cheeta.server.util.facade.UserCache;
import io.cheeta.server.web.WebConstants;
import io.cheeta.server.web.component.select2.Response;
import io.cheeta.server.web.component.select2.ResponseFiller;
import io.cheeta.server.web.component.user.choice.AbstractUserChoiceProvider;

import java.util.ArrayList;
import java.util.List;

public abstract class AssigneeProvider extends AbstractUserChoiceProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public void query(String term, int page, Response<User> response) {
		PullRequest request = getPullRequest();
		UserService userService = Cheeta.getInstance(UserService.class);

		List<User> users = new ArrayList<>(SecurityUtils.getAuthorizedUsers(request.getProject(), new WriteCode()));
		
		users.removeAll(request.getAssignees());
		
		UserCache cache = userService.cloneCache();
		users.sort(cache.comparingDisplayName(request.getParticipants()));

		new ResponseFiller<>(response).fill(new Similarities<>(users) {

			private static final long serialVersionUID = 1L;

			@Override
			public double getSimilarScore(User object) {
				return cache.getSimilarScore(object, term);
			}

		}, page, WebConstants.PAGE_SIZE);
	}

	protected abstract PullRequest getPullRequest();
	
}
package io.cheeta.server.buildspecmodel.inputspec.userchoiceinput.choiceprovider;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Sets;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.UserService;
import io.cheeta.server.model.User;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.util.facade.UserCache;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.web.page.project.issues.detail.IssueDetailPage;
import io.cheeta.server.web.util.WicketUtils;

@Editable(order=130, name="All users")
public class AllUsers implements ChoiceProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public List<User> getChoices(boolean allPossible) {
		UserCache cache = Cheeta.getInstance(UserService.class).cloneCache();
		
		if (WicketUtils.getPage() instanceof IssueDetailPage) {
			IssueDetailPage issueDetailPage = (IssueDetailPage) WicketUtils.getPage();
			List<User> users = new ArrayList<>(cache.getUsers());
			users.sort(cache.comparingDisplayName(issueDetailPage.getIssue().getParticipants()));
			return users;
		} else if (SecurityUtils.getAuthUser() != null) {
			List<User> users = new ArrayList<>(cache.getUsers());
			users.sort(cache.comparingDisplayName(Sets.newHashSet(SecurityUtils.getAuthUser())));
			return users;
		} else {
			List<User> users = new ArrayList<>(cache.getUsers());
			users.sort(cache.comparingDisplayName(Sets.newHashSet()));
			return users;
		}
	}

}

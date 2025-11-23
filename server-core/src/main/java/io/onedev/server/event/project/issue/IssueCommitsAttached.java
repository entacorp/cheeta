package io.cheeta.server.event.project.issue;

import java.util.Date;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.UserService;
import io.cheeta.server.model.Issue;

public class IssueCommitsAttached extends IssueEvent {

	private static final long serialVersionUID = 1L;
	
	public IssueCommitsAttached(Issue issue) {
		super(Cheeta.getInstance(UserService.class).getSystem(), new Date(), issue);
	}

	@Override
	public boolean affectsListing() {
		return false;
	}
	
	@Override
	public String getActivity() {
		return "commits attached";
	}

	@Override
	public boolean isMinor() {
		return true;
	}
	
}

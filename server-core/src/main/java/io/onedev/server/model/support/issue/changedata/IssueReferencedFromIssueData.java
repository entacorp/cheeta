package io.cheeta.server.model.support.issue.changedata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.IssueService;
import io.cheeta.server.entityreference.ReferencedFromAware;
import io.cheeta.server.model.Group;
import io.cheeta.server.model.Issue;
import io.cheeta.server.model.User;
import io.cheeta.server.notification.ActivityDetail;

public class IssueReferencedFromIssueData extends IssueChangeData implements ReferencedFromAware<Issue> {

	private static final long serialVersionUID = 1L;

	private final Long issueId;
	
	public IssueReferencedFromIssueData(Issue issue) {
		this.issueId = issue.getId();
	}
	
	public Long getIssueId() {
		return issueId;
	}

	@Override
	public String getActivity() {
		return "referenced from other issue";
	}

	@Override
	public Map<String, Collection<User>> getNewUsers() {
		return new HashMap<>();
	}

	@Override
	public Map<String, Group> getNewGroups() {
		return new HashMap<>();
	}

	@Override
	public boolean affectsListing() {
		return false;
	}

	@Override
	public boolean isMinor() {
		return true;
	}

	@Override
	public Issue getReferencedFrom() {
		return Cheeta.getInstance(IssueService.class).get(issueId);
	}

	@Override
	public ActivityDetail getActivityDetail() {
		return ActivityDetail.referencedFrom(getReferencedFrom());
	}

}

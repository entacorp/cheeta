package io.cheeta.server.model.support.issue.changedata;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.CodeCommentService;
import io.cheeta.server.entityreference.ReferencedFromAware;
import io.cheeta.server.model.CodeComment;
import io.cheeta.server.model.Group;
import io.cheeta.server.model.User;
import io.cheeta.server.notification.ActivityDetail;

public class IssueReferencedFromCodeCommentData extends IssueChangeData implements ReferencedFromAware<CodeComment> {

	private static final long serialVersionUID = 1L;

	private final Long commentId;
	
	public IssueReferencedFromCodeCommentData(CodeComment comment) {
		this.commentId = comment.getId();
	}
	
	public Long getCommentId() {
		return commentId;
	}

	@Override
	public String getActivity() {
		return "referenced from code comment";
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
	public boolean isMinor() {
		return true;
	}

	@Override
	public boolean affectsListing() {
		return false;
	}

	@Override
	public ActivityDetail getActivityDetail() {
		return ActivityDetail.referencedFrom(getReferencedFrom());
	}

	@Override
	public CodeComment getReferencedFrom() {
		return Cheeta.getInstance(CodeCommentService.class).get(commentId);
	}

}

package io.cheeta.server.model.support.pullrequest.changedata;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.CodeCommentService;
import io.cheeta.server.entityreference.ReferencedFromAware;
import io.cheeta.server.model.CodeComment;
import io.cheeta.server.notification.ActivityDetail;

public class PullRequestReferencedFromCodeCommentData 
		extends PullRequestChangeData implements ReferencedFromAware<CodeComment> {

	private static final long serialVersionUID = 1L;

	private final Long commentId;
	
	public PullRequestReferencedFromCodeCommentData(CodeComment comment) {
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
	public boolean isMinor() {
		return true;
	}

	@Override
	public CodeComment getReferencedFrom() {
		return Cheeta.getInstance(CodeCommentService.class).get(commentId);
	}

	@Override
	public ActivityDetail getActivityDetail() {
		return ActivityDetail.referencedFrom(getReferencedFrom());
	}
	
}

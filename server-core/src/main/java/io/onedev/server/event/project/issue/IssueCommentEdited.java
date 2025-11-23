package io.cheeta.server.event.project.issue;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.IssueCommentService;
import io.cheeta.server.model.IssueComment;
import io.cheeta.server.security.SecurityUtils;

import java.util.Date;

public class IssueCommentEdited extends IssueEvent {

	private static final long serialVersionUID = 1L;
	
	private final Long commentId;
	
	public IssueCommentEdited(IssueComment comment) {
		super(SecurityUtils.getUser(), new Date(), comment.getIssue());
		this.commentId = comment.getId();
	}
	
	public IssueComment getComment() {
		return Cheeta.getInstance(IssueCommentService.class).load(commentId);
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
	public String getActivity() {
		return "comment edited";
	}

}

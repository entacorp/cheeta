package io.cheeta.server.event.project.pullrequest;

import java.util.Date;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.CodeCommentService;
import io.cheeta.server.model.CodeComment;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.User;

public abstract class PullRequestCodeCommentEvent extends PullRequestEvent {

	private static final long serialVersionUID = 1L;
	
	private final Long commentId;
	
	public PullRequestCodeCommentEvent(User user, Date date, PullRequest request, CodeComment comment) {
		super(user, date, request);
		commentId = comment.getId();
	}

	public CodeComment getComment() {
		return Cheeta.getInstance(CodeCommentService.class).load(commentId);
	}

}

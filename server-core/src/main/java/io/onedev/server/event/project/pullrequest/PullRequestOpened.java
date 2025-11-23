package io.cheeta.server.event.project.pullrequest;

import io.cheeta.server.model.PullRequest;
import io.cheeta.server.util.commenttext.CommentText;
import io.cheeta.server.util.commenttext.MarkdownText;

public class PullRequestOpened extends PullRequestEvent {

	private static final long serialVersionUID = 1L;

	public PullRequestOpened(PullRequest request) {
		super(request.getSubmitter(), request.getSubmitDate(), request);
	}

	@Override
	protected CommentText newCommentText() {
		return getRequest().getDescription()!=null? new MarkdownText(getProject(), getRequest().getDescription()): null;
	}

	@Override
	public String getActivity() {
		return "opened";
	}

}

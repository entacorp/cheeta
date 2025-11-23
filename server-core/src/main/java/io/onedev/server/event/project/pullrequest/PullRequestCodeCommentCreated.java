package io.cheeta.server.event.project.pullrequest;

import io.cheeta.server.Cheeta;
import io.cheeta.server.model.CodeComment;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.util.commenttext.CommentText;
import io.cheeta.server.util.commenttext.MarkdownText;
import io.cheeta.server.web.UrlService;

public class PullRequestCodeCommentCreated extends PullRequestCodeCommentEvent {

	private static final long serialVersionUID = 1L;

	public PullRequestCodeCommentCreated(PullRequest request, CodeComment comment) {
		super(comment.getUser(), comment.getCreateDate(), request, comment);
	}

	@Override
	protected CommentText newCommentText() {
		return new MarkdownText(getProject(), getComment().getContent());
	}

	@Override
	public String getActivity() {
		return "created code comment"; 
	}

	@Override
	public String getUrl() {
		return Cheeta.getInstance(UrlService.class).urlFor(getComment(), true);
	}

}

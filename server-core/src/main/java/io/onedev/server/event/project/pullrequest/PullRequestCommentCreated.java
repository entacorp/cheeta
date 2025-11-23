package io.cheeta.server.event.project.pullrequest;

import java.util.Collection;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.PullRequestCommentService;
import io.cheeta.server.model.PullRequestComment;
import io.cheeta.server.util.commenttext.CommentText;
import io.cheeta.server.util.commenttext.MarkdownText;
import io.cheeta.server.web.UrlService;

public class PullRequestCommentCreated extends PullRequestEvent {

	private static final long serialVersionUID = 1L;

	private final Long commentId;
	
	private final Collection<String> notifiedEmailAddresses;
	
	public PullRequestCommentCreated(PullRequestComment comment, Collection<String> notifiedEmailAddresses) {
		super(comment.getUser(), comment.getDate(), comment.getRequest());
		commentId = comment.getId();
		this.notifiedEmailAddresses = notifiedEmailAddresses;
	}

	public PullRequestComment getComment() {
		return Cheeta.getInstance(PullRequestCommentService.class).load(commentId);
	}

	public Collection<String> getNotifiedEmailAddresses() {
		return notifiedEmailAddresses;
	}

	@Override
	protected CommentText newCommentText() {
		return new MarkdownText(getProject(), getComment().getContent());
	}

	@Override
	public String getActivity() {
		return "commented";
	}

	@Override
	public String getUrl() {
		return Cheeta.getInstance(UrlService.class).urlFor(getComment(), true);
	}

}

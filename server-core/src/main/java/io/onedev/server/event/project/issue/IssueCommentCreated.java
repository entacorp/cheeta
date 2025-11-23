package io.cheeta.server.event.project.issue;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.IssueCommentService;
import io.cheeta.server.web.UrlService;
import io.cheeta.server.model.IssueComment;
import io.cheeta.server.util.commenttext.CommentText;
import io.cheeta.server.util.commenttext.MarkdownText;

import java.util.Collection;

public class IssueCommentCreated extends IssueEvent {

	private static final long serialVersionUID = 1L;

	private final Long commentId;
	
	private final Collection<String> notifiedEmailAddresses;
	
	public IssueCommentCreated(IssueComment comment, Collection<String> notifiedEmailAddresses) {
		super(comment.getUser(), comment.getDate(), comment.getIssue());
		commentId = comment.getId();
		this.notifiedEmailAddresses = notifiedEmailAddresses;
	}

	public IssueComment getComment() {
		return Cheeta.getInstance(IssueCommentService.class).load(commentId);
	}

	@Override
	protected CommentText newCommentText() {
		return new MarkdownText(getProject(), getComment().getContent());
	}

	@Override
	public boolean affectsListing() {
		return false;
	}

	public Collection<String> getNotifiedEmailAddresses() {
		return notifiedEmailAddresses;
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

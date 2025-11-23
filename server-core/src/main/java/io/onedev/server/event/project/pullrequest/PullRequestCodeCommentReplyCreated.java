package io.cheeta.server.event.project.pullrequest;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.CodeCommentReplyService;
import io.cheeta.server.model.CodeCommentReply;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.util.commenttext.CommentText;
import io.cheeta.server.util.commenttext.MarkdownText;
import io.cheeta.server.web.UrlService;

public class PullRequestCodeCommentReplyCreated extends PullRequestCodeCommentEvent {

	private static final long serialVersionUID = 1L;
	
	private final Long replyId;
	
	public PullRequestCodeCommentReplyCreated(PullRequest request, CodeCommentReply reply) {
		super(reply.getUser(), reply.getDate(), request, reply.getComment());
		replyId = reply.getId();
	}

	public CodeCommentReply getReply() {
		return Cheeta.getInstance(CodeCommentReplyService.class).load(replyId);
	}

	@Override
	protected CommentText newCommentText() {
		return new MarkdownText(getProject(), getReply().getContent());
	}

	@Override
	public String getActivity() {
		return "replied code comment"; 
	}

	@Override
	public String getUrl() {
		return Cheeta.getInstance(UrlService.class).urlFor(getReply(), true);
	}

}

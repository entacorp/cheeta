package io.cheeta.server.event.project.codecomment;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.CodeCommentReplyService;
import io.cheeta.server.web.UrlService;
import io.cheeta.server.model.CodeCommentReply;
import io.cheeta.server.util.commenttext.CommentText;
import io.cheeta.server.util.commenttext.MarkdownText;

public class CodeCommentReplyCreated extends CodeCommentEvent {

	private static final long serialVersionUID = 1L;

	private final Long replyId;
	
	public CodeCommentReplyCreated(CodeCommentReply reply) {
		super(reply.getUser(), reply.getDate(), reply.getComment());
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
		return "replied";
	}

	@Override
	public String getUrl() {
		return Cheeta.getInstance(UrlService.class).urlFor(getReply(), true);
	}

}

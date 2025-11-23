package io.cheeta.server.event.project.codecomment;

import io.cheeta.server.model.CodeCommentReply;
import io.cheeta.server.security.SecurityUtils;

import java.util.Date;

public class CodeCommentReplyDeleted extends CodeCommentEvent {

	private final Long replyId;
	
	private static final long serialVersionUID = 1L;
	
	public CodeCommentReplyDeleted(CodeCommentReply reply) {
		super(SecurityUtils.getUser(), new Date(), reply.getComment());
		this.replyId = reply.getId();
	}
	
	public Long getReplyId() {
		return replyId;
	}

	@Override
	public boolean isMinor() {
		return true;
	}

	@Override
	public String getActivity() {
		return "reply deleted";
	}

}

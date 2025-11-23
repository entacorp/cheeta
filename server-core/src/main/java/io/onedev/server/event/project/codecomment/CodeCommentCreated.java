package io.cheeta.server.event.project.codecomment;

import io.cheeta.server.model.CodeComment;
import io.cheeta.server.util.commenttext.CommentText;
import io.cheeta.server.util.commenttext.MarkdownText;

public class CodeCommentCreated extends CodeCommentEvent {

	private static final long serialVersionUID = 1L;

	public CodeCommentCreated(CodeComment comment) {
		super(comment.getUser(), comment.getCreateDate(), comment);
	}

	@Override
	protected CommentText newCommentText() {
		return new MarkdownText(getProject(), getComment().getContent());
	}

	@Override
	public String getActivity() {
		return "added";
	}

}

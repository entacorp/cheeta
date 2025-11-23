package io.cheeta.server.event.project.codecomment;

import java.util.Date;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.CodeCommentService;
import io.cheeta.server.web.UrlService;
import io.cheeta.server.event.project.ProjectEvent;
import io.cheeta.server.model.CodeComment;
import io.cheeta.server.model.User;

public abstract class CodeCommentEvent extends ProjectEvent {

	private static final long serialVersionUID = 1L;
	
	private final Long commentId;
	
	/**
	 * @param comment
	 * @param user
	 * @param date
	 */
	public CodeCommentEvent(User user, Date date, CodeComment comment) {
		super(user, date, comment.getProject());
		commentId = comment.getId();
	}

	public CodeComment getComment() {
		return Cheeta.getInstance(CodeCommentService.class).load(commentId);
	}

	@Override
	public String getUrl() {
		return Cheeta.getInstance(UrlService.class).urlFor(getComment(), true);
	}
	
}

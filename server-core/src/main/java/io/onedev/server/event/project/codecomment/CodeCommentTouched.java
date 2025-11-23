package io.cheeta.server.event.project.codecomment;

import java.util.Date;

import io.cheeta.server.event.project.ProjectEvent;
import io.cheeta.server.model.Project;
import io.cheeta.server.security.SecurityUtils;

public class CodeCommentTouched extends ProjectEvent {
	
	private static final long serialVersionUID = 1L;
	
	private final Long commentId;
	
	public CodeCommentTouched(Project project, Long commentId) {
		super(SecurityUtils.getUser(), new Date(), project);
		this.commentId = commentId;
	}
	
	public Long getCommentId() {
		return commentId;
	}

	@Override
	public String getActivity() {
		return "touched";
	}
	
}

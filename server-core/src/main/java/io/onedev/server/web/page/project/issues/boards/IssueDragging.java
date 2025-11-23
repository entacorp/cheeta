package io.cheeta.server.web.page.project.issues.boards;

import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;

import io.cheeta.server.model.Issue;
import io.cheeta.server.web.util.AjaxPayload;

public class IssueDragging extends AjaxPayload {

	private final Issue issue;
	
	public IssueDragging(IPartialPageRequestHandler handler, Issue issue) {
		super(handler);
		this.issue = issue;
	}

	public Issue getIssue() {
		return issue;
	}

}

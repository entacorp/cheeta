package io.cheeta.server.web.page.project.pullrequests.detail.activities.activity;

import java.util.Date;

import org.apache.wicket.Component;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.PullRequestCommentService;
import io.cheeta.server.model.PullRequestComment;
import io.cheeta.server.web.page.project.pullrequests.detail.activities.PullRequestActivity;

public class PullRequestCommentActivity implements PullRequestActivity {

	private final Long commentId;

	public PullRequestCommentActivity(PullRequestComment comment) {
		commentId = comment.getId();
	}
	
	@Override
	public Component render(String componentId) {
		return new PullRequestCommentPanel(componentId);
	}

	
	public PullRequestComment getComment() {
		return Cheeta.getInstance(PullRequestCommentService.class).load(commentId);
	}

	@Override
	public Date getDate() {
		return getComment().getDate();
	}

}

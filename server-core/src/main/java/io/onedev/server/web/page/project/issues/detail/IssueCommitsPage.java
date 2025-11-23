package io.cheeta.server.web.page.project.issues.detail;

import org.apache.wicket.request.mapper.parameter.PageParameters;

import io.cheeta.server.web.component.issue.commits.IssueCommitsPanel;

public class IssueCommitsPage extends IssueDetailPage {

	public IssueCommitsPage(PageParameters params) {
		super(params);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(new IssueCommitsPanel("commits", issueModel));
	}
	
}

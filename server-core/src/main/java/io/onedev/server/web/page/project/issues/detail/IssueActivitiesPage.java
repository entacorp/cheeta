package io.cheeta.server.web.page.project.issues.detail;

import org.apache.wicket.Component;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import io.cheeta.server.model.Issue;
import io.cheeta.server.web.component.issue.activities.IssueActivitiesPanel;

public class IssueActivitiesPage extends IssueDetailPage {

	private IssueActivitiesPanel activities;
	
	public IssueActivitiesPage(PageParameters params) {
		super(params);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		add(activities = new IssueActivitiesPanel("activities") {

			@Override
			protected Issue getIssue() {
				return IssueActivitiesPage.this.getIssue();
			}
			
		});
	}

	public Component renderOptions(String componentId) {
		return activities.renderOptions(componentId);
	}
	
}

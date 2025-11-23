package io.cheeta.server.web.page.project.issues;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.model.support.administration.GlobalIssueSetting;
import io.cheeta.server.web.component.issue.workflowreconcile.WorkflowChangeAlertPanel;
import io.cheeta.server.web.page.project.ProjectPage;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.request.mapper.parameter.PageParameters;

public abstract class ProjectIssuesPage extends ProjectPage {

	public ProjectIssuesPage(PageParameters params) {
		super(params);
	}

	protected GlobalIssueSetting getIssueSetting() {
		return Cheeta.getInstance(SettingService.class).getIssueSetting();
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();

		add(new WorkflowChangeAlertPanel("workflowChangeAlert") {

			@Override
			protected void onCompleted(AjaxRequestTarget target) {
				setResponsePage(getPageClass(), getPageParameters());
			}
			
		});
	}

	@Override
	protected String getPageTitle() {
		return "Issues - " + getProject().getPath();
	}
	
}

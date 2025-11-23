package io.cheeta.server.web.component.issue.referencedfrom;

import static org.unbescape.html.HtmlEscape.escapeHtml5;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.cycle.RequestCycle;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.IssueService;
import io.cheeta.server.model.Issue;
import io.cheeta.server.model.Project;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.web.component.issue.IssueStateBadge;
import io.cheeta.server.web.page.project.ProjectPage;
import io.cheeta.server.web.page.project.issues.detail.IssueActivitiesPage;

public class ReferencedFromIssuePanel extends GenericPanel<Issue> {

	public ReferencedFromIssuePanel(String id, Long issueId) {
		super(id, new LoadableDetachableModel<Issue>() {

			@Override
			protected Issue load() {
				return Cheeta.getInstance(IssueService.class).load(issueId);
			}
			
		});
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		add(new IssueStateBadge("state", getModel(), false));
		
		Project project = ((ProjectPage)getPage()).getProject();
		
		Issue issue = getModelObject();
		
		if (SecurityUtils.canAccessProject(issue.getProject())) {
			String url = RequestCycle.get().urlFor(IssueActivitiesPage.class, 
					IssueActivitiesPage.paramsOf(issue)).toString();
			String summary = String.format("<a href='%s'>%s</a>", 
					url, escapeHtml5(issue.getSummary(project)));
			add(new Label("summary", summary).setEscapeModelStrings(false));
		} else {
			add(new Label("summary", issue.getSummary(project)));
		}
	}

}

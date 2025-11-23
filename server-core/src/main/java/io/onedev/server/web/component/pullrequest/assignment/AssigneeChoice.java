package io.cheeta.server.web.component.pullrequest.assignment;

import io.cheeta.server.web.page.base.BasePage;

import static io.cheeta.server.web.translation.Translation._T;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.PullRequestAssignmentService;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.PullRequestAssignment;
import io.cheeta.server.model.User;
import io.cheeta.server.web.component.select2.SelectToActChoice;
import io.cheeta.server.web.component.user.choice.UserChoiceResourceReference;

public abstract class AssigneeChoice extends SelectToActChoice<User> {

	public AssigneeChoice(String id) {
		super(id);
		
		setProvider(new AssigneeProvider() {

			@Override
			protected PullRequest getPullRequest() {
				return AssigneeChoice.this.getPullRequest();
			}
			
		});
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		getSettings().setPlaceholder(_T("Add assignee..."));
		getSettings().setFormatResult("cheeta.server.userChoiceFormatter.formatResult");
		getSettings().setFormatSelection("cheeta.server.userChoiceFormatter.formatSelection");
		getSettings().setEscapeMarkup("cheeta.server.userChoiceFormatter.escapeMarkup");
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		
		response.render(JavaScriptHeaderItem.forReference(new UserChoiceResourceReference()));
	}

	@Override
	protected void onSelect(AjaxRequestTarget target, User user) {
		PullRequestAssignment assignment = new PullRequestAssignment();
		assignment.setRequest(getPullRequest());
		assignment.setUser(user);

		if (!getPullRequest().isNew()) {
			Cheeta.getInstance(PullRequestAssignmentService.class).create(assignment);
			((BasePage)getPage()).notifyObservableChange(target,
					PullRequest.getChangeObservable(getPullRequest().getId()));
		} else {
			getPullRequest().getAssignments().add(assignment);
		}
	};
	
	protected abstract PullRequest getPullRequest();
}

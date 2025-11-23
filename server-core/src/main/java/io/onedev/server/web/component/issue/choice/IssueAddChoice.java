package io.cheeta.server.web.component.issue.choice;

import static io.cheeta.server.web.translation.Translation._T;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;

import io.cheeta.server.model.Issue;
import io.cheeta.server.web.component.select2.SelectToActChoice;

public abstract class IssueAddChoice extends SelectToActChoice<Issue> {

	public IssueAddChoice(String id, IssueChoiceProvider choiceProvider) {
		super(id, choiceProvider);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		getSettings().setPlaceholder(getPlaceholder());
		getSettings().setFormatResult("cheeta.server.issueChoiceFormatter.formatResult");
		getSettings().setFormatSelection("cheeta.server.issueChoiceFormatter.formatSelection");
		getSettings().setEscapeMarkup("cheeta.server.issueChoiceFormatter.escapeMarkup");
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		
		response.render(JavaScriptHeaderItem.forReference(new IssueChoiceResourceReference()));
	}

	protected String getPlaceholder() {
		return _T("Add Issue...");
	}
	
}

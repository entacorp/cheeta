package io.cheeta.server.web.component.branch.choice;

import static io.cheeta.server.web.translation.Translation._T;

import java.util.Collection;
import java.util.List;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.IModel;

import io.cheeta.server.web.component.select2.Select2MultiChoice;
import io.cheeta.server.web.component.stringchoice.StringChoiceProvider;

public class BranchMultiChoice extends Select2MultiChoice<String> {

	public BranchMultiChoice(String id, IModel<Collection<String>> model, 
			IModel<List<String>> choicesModel, boolean tagsMode) {
		super(id, model, new StringChoiceProvider(choicesModel, tagsMode));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		if (isRequired())
			getSettings().setPlaceholder(_T("Choose branches..."));
		else
			getSettings().setPlaceholder(_T("Not specified"));
		getSettings().setFormatResult("cheeta.server.branchChoiceFormatter.formatResult");
		getSettings().setFormatSelection("cheeta.server.branchChoiceFormatter.formatSelection");
		getSettings().setEscapeMarkup("cheeta.server.branchChoiceFormatter.escapeMarkup");
		setConvertEmptyInputStringToNull(true);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(new BranchChoiceResourceReference()));
	}
	
}

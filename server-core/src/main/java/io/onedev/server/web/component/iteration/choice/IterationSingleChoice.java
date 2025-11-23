package io.cheeta.server.web.component.iteration.choice;

import static io.cheeta.server.web.translation.Translation._T;

import java.util.Collection;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.IModel;

import io.cheeta.server.model.Iteration;
import io.cheeta.server.web.component.select2.Select2Choice;

public class IterationSingleChoice extends Select2Choice<Iteration> {

	public IterationSingleChoice(String id, IModel<Iteration> selectionModel, IModel<Collection<Iteration>> iterationsModel) {
		super(id, selectionModel, new IterationChoiceProvider(iterationsModel));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		getSettings().setAllowClear(!isRequired());
		if (isRequired())
			getSettings().setPlaceholder(_T("Choose iteration..."));
		else
			getSettings().setPlaceholder(_T("Not specified"));
		getSettings().setFormatResult("cheeta.server.iterationChoiceFormatter.formatResult");
		getSettings().setFormatSelection("cheeta.server.iterationChoiceFormatter.formatSelection");
		getSettings().setEscapeMarkup("cheeta.server.iterationChoiceFormatter.escapeMarkup");
		setConvertEmptyInputStringToNull(true);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		
		response.render(JavaScriptHeaderItem.forReference(new IterationChoiceResourceReference()));
	}
	
}

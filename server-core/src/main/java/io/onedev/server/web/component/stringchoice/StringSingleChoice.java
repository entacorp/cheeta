package io.cheeta.server.web.component.stringchoice;

import java.util.List;
import java.util.Map;

import org.apache.wicket.model.IModel;

import io.cheeta.server.web.component.select2.Select2Choice;

public class StringSingleChoice extends Select2Choice<String> {

	public StringSingleChoice(String id, IModel<String> selectionModel, 
			IModel<List<String>> choicesModel, 
			IModel<Map<String, String>> displayNamesModel, 
			IModel<Map<String, String>> descriptionsModel,
			boolean tagsMode) {
		super(id, selectionModel, new StringChoiceProvider(choicesModel, displayNamesModel, descriptionsModel, tagsMode));
	}

	public StringSingleChoice(String id, IModel<String> selectionModel, 
			IModel<List<String>> choicesModel, 
			IModel<Map<String, String>> displayNamesModel,
			boolean tagsMode) {
		super(id, selectionModel, new StringChoiceProvider(choicesModel, displayNamesModel, tagsMode));
	}

	public StringSingleChoice(String id, IModel<String> selectionModel, IModel<List<String>> choicesModel, boolean tagsMode) {
		super(id, selectionModel, new StringChoiceProvider(choicesModel, tagsMode));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		getSettings().setAllowClear(!isRequired());
		getSettings().setFormatResult("cheeta.server.choiceFormatter.formatResult");
		getSettings().setFormatSelection("cheeta.server.choiceFormatter.formatSelection");
		getSettings().setEscapeMarkup("cheeta.server.choiceFormatter.escapeMarkup");
		setConvertEmptyInputStringToNull(true);
	}

}

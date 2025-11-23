package io.cheeta.server.web.component.user.choice;

import static io.cheeta.server.web.translation.Translation._T;

import java.util.List;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.IModel;

import io.cheeta.server.model.User;
import io.cheeta.server.web.component.select2.Select2Choice;

public class UserSingleChoice extends Select2Choice<User> {

	public UserSingleChoice(String id, IModel<User> selectionModel, IModel<List<User>> choicesModel) {
		super(id, selectionModel, new UserChoiceProvider(choicesModel));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		getSettings().setAllowClear(!isRequired());
		if (isRequired())
			getSettings().setPlaceholder(_T("Choose a user..."));
		else
			getSettings().setPlaceholder(_T("Not specified"));
		getSettings().setFormatResult("cheeta.server.userChoiceFormatter.formatResult");
		getSettings().setFormatSelection("cheeta.server.userChoiceFormatter.formatSelection");
		getSettings().setEscapeMarkup("cheeta.server.userChoiceFormatter.escapeMarkup");
		setConvertEmptyInputStringToNull(true);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		
		response.render(JavaScriptHeaderItem.forReference(new UserChoiceResourceReference()));
	}
	
}

package io.cheeta.server.web.component.rolechoice;

import static io.cheeta.server.web.translation.Translation._T;

import java.util.Collection;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.IModel;

import io.cheeta.server.model.Role;
import io.cheeta.server.web.component.select2.Select2Choice;

public class RoleSingleChoice extends Select2Choice<Role> {

	public RoleSingleChoice(String id, IModel<Role> selectionModel, IModel<Collection<Role>> choicesModel) {
		super(id, selectionModel, new RoleChoiceProvider(choicesModel));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		getSettings().setAllowClear(!isRequired());
		if (isRequired())
			getSettings().setPlaceholder(_T("Choose role..."));
		else
			getSettings().setPlaceholder(_T("Not specified"));
		getSettings().setFormatResult("cheeta.server.roleChoiceFormatter.formatResult");
		getSettings().setFormatSelection("cheeta.server.roleChoiceFormatter.formatSelection");
		getSettings().setEscapeMarkup("cheeta.server.roleChoiceFormatter.escapeMarkup");
		setConvertEmptyInputStringToNull(true);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		
		response.render(JavaScriptHeaderItem.forReference(new RoleChoiceResourceReference()));
	}
	
}
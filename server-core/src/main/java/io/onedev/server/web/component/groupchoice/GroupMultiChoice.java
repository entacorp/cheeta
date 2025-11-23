package io.cheeta.server.web.component.groupchoice;

import static io.cheeta.server.web.translation.Translation._T;

import java.util.Collection;

import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.model.IModel;

import io.cheeta.server.model.Group;
import io.cheeta.server.web.component.select2.Select2MultiChoice;

public class GroupMultiChoice extends Select2MultiChoice<Group> {

	public GroupMultiChoice(String id, IModel<Collection<Group>> selectionsModel, IModel<Collection<Group>> choicesModel) {
		super(id, selectionsModel, new GroupChoiceProvider(choicesModel));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		if (isRequired())
			getSettings().setPlaceholder(_T("Choose groups..."));
		else
			getSettings().setPlaceholder(_T("Not specified"));
		getSettings().setFormatResult("cheeta.server.groupChoiceFormatter.formatResult");
		getSettings().setFormatSelection("cheeta.server.groupChoiceFormatter.formatSelection");
		getSettings().setEscapeMarkup("cheeta.server.groupChoiceFormatter.escapeMarkup");
		setConvertEmptyInputStringToNull(true);
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		
		response.render(JavaScriptHeaderItem.forReference(new GroupChoiceResourceReference()));
	}

}

package io.cheeta.server.buildspec.param.spec.userchoiceparam.defaultmultivalueprovider;

import com.google.common.collect.Lists;
import io.cheeta.server.Cheeta;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.OmitName;
import io.cheeta.server.buildspecmodel.inputspec.userchoiceinput.choiceprovider.ChoiceProvider;
import io.cheeta.server.model.User;
import io.cheeta.server.util.EditContext;

import javax.validation.Validator;
import javax.validation.constraints.NotEmpty;
import java.util.List;
import java.util.stream.Collectors;

@Editable(order=100, name="Use specified default value")
public class SpecifiedDefaultMultiValue implements DefaultMultiValueProvider {

	private static final long serialVersionUID = 1L;

	private List<String> value;

	@Editable(name="Literal default value")
	@io.cheeta.server.annotation.ChoiceProvider("getValueChoices")
	@NotEmpty
	@OmitName
	public List<String> getValue() {
		return value;
	}

	public void setValue(List<String> value) {
		this.value = value;
	}

	@Override
	public List<String> getDefaultValue() {
		return getValue();
	}

	@SuppressWarnings("unused")
	private static List<String> getValueChoices() {
		ChoiceProvider choiceProvider = (ChoiceProvider) EditContext.get(1).getInputValue("choiceProvider");
		if (choiceProvider != null && Cheeta.getInstance(Validator.class).validate(choiceProvider).isEmpty())
			return choiceProvider.getChoices(true).stream().map(User::getName).collect(Collectors.toList());
		else
			return Lists.newArrayList();
	}

}

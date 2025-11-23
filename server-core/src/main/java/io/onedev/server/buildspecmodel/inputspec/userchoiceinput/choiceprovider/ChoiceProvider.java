package io.cheeta.server.buildspecmodel.inputspec.userchoiceinput.choiceprovider;

import java.io.Serializable;
import java.util.List;

import io.cheeta.server.model.User;
import io.cheeta.server.annotation.Editable;

@Editable
public interface ChoiceProvider extends Serializable {
	
	List<User> getChoices(boolean allPossible);
	
}

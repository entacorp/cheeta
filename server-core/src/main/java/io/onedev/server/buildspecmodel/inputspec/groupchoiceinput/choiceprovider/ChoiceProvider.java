package io.cheeta.server.buildspecmodel.inputspec.groupchoiceinput.choiceprovider;

import java.io.Serializable;
import java.util.List;

import io.cheeta.server.model.Group;
import io.cheeta.server.annotation.Editable;

@Editable
public interface ChoiceProvider extends Serializable {
	
	List<Group> getChoices(boolean allPossible);
	
}

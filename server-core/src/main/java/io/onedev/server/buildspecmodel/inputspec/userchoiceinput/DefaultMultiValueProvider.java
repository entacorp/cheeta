package io.cheeta.server.buildspecmodel.inputspec.userchoiceinput;

import java.io.Serializable;
import java.util.List;

import io.cheeta.server.annotation.Editable;

@Editable
public interface DefaultMultiValueProvider extends Serializable {
	
	List<String> getDefaultValue();
	
}

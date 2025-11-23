package io.cheeta.server.buildspecmodel.inputspec.choiceinput;

import java.io.Serializable;

import io.cheeta.server.annotation.Editable;

@Editable
public interface DefaultValueProvider extends Serializable {
	
	String getDefaultValue();
	
}

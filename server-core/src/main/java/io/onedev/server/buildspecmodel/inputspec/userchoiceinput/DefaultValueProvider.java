package io.cheeta.server.buildspecmodel.inputspec.userchoiceinput;

import java.io.Serializable;

import io.cheeta.server.annotation.Editable;

@Editable
public interface DefaultValueProvider extends Serializable {
	
	String getDefaultValue();
	
}

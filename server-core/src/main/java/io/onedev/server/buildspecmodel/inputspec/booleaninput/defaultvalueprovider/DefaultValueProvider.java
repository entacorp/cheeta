package io.cheeta.server.buildspecmodel.inputspec.booleaninput.defaultvalueprovider;

import java.io.Serializable;

import io.cheeta.server.annotation.Editable;

@Editable
public interface DefaultValueProvider extends Serializable {
	
	boolean getDefaultValue();
	
}

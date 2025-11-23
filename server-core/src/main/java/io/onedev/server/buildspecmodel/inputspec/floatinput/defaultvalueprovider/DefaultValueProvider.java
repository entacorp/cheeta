package io.cheeta.server.buildspecmodel.inputspec.floatinput.defaultvalueprovider;

import java.io.Serializable;

import io.cheeta.server.annotation.Editable;

@Editable
public interface DefaultValueProvider extends Serializable {
	
	float getDefaultValue();
	
}

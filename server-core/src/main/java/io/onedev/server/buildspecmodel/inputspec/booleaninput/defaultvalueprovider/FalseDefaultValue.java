package io.cheeta.server.buildspecmodel.inputspec.booleaninput.defaultvalueprovider;

import io.cheeta.server.annotation.Editable;

@Editable(order=200, name="false")
public class FalseDefaultValue implements DefaultValueProvider {

	private static final long serialVersionUID = 1L;

	@Override
	public boolean getDefaultValue() {
		return false;
	}

}

package io.cheeta.server.buildspecmodel.inputspec.dateinput.defaultvalueprovider;

import java.io.Serializable;
import java.util.Date;

import io.cheeta.server.annotation.Editable;

@Editable
public interface DefaultValueProvider extends Serializable {
	
	Date getDefaultValue();
	
}

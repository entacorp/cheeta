package io.cheeta.server.buildspecmodel.inputspec.showcondition;

import java.io.Serializable;
import java.util.List;

import io.cheeta.server.annotation.Editable;

@Editable
public interface ValueMatcher extends Serializable {
	
	boolean matches(List<String> values);
	
}

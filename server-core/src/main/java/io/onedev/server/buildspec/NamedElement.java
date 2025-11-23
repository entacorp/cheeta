package io.cheeta.server.buildspec;

import io.cheeta.server.annotation.Editable;

import java.io.Serializable;

@Editable
public interface NamedElement extends Serializable {

	String getName();
	
}

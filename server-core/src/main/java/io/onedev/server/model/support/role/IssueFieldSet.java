package io.cheeta.server.model.support.role;

import java.io.Serializable;
import java.util.Collection;

import io.cheeta.server.annotation.Editable;

@Editable
public interface IssueFieldSet extends Serializable {

	Collection<String> getIncludeFields();
	
}

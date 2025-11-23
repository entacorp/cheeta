package io.cheeta.server.util.usermatch;

import java.io.Serializable;

import io.cheeta.server.annotation.Editable;
import io.cheeta.server.model.User;

@Editable
public interface UserMatchCriteria extends Serializable {
	
	boolean matches(User user);
	
}

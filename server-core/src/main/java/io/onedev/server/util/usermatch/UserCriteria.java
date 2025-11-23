package io.cheeta.server.util.usermatch;

import io.cheeta.commons.utils.StringUtils;
import io.cheeta.server.model.User;

public class UserCriteria implements UserMatchCriteria {

	private static final long serialVersionUID = 1L;
	
	private final User user;

	public UserCriteria(User user) {
		this.user = user;
	}
	
	public User getUser() {
		return user;
	}

	@Override
	public boolean matches(User user) {
		return this.user.equals(user);
	}

	@Override
	public String toString() {
		return "user(" + StringUtils.escape(user.getName(), "()") + ")";
	}
	
}

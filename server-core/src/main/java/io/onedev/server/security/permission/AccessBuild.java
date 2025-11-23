package io.cheeta.server.security.permission;

import io.cheeta.server.util.facade.UserFacade;
import org.apache.shiro.authz.Permission;
import org.jetbrains.annotations.Nullable;

public class AccessBuild implements BasePermission {

	@Override
	public boolean implies(Permission p) {
		return p instanceof AccessBuild;
	}

	@Override
	public boolean isApplicable(@Nullable UserFacade user) {
		return true;
	}
	
}

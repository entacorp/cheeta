package io.cheeta.server.security.permission;

import io.cheeta.server.util.facade.UserFacade;
import org.apache.shiro.authz.Permission;
import org.jetbrains.annotations.Nullable;

public class UploadCache implements BasePermission {

	@Override
	public boolean implies(Permission p) {
		return p instanceof UploadCache;
	}

	@Override
	public boolean isApplicable(@Nullable UserFacade user) {
		return user != null;
	}
	
}

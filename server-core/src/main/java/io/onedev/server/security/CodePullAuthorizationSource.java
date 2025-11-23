package io.cheeta.server.security;

import javax.servlet.http.HttpServletRequest;

import io.cheeta.server.model.Project;

public interface CodePullAuthorizationSource {

	boolean canPullCode(HttpServletRequest request, Project project);
	
}

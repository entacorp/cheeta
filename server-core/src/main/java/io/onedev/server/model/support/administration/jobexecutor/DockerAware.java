package io.cheeta.server.model.support.administration.jobexecutor;

import io.cheeta.k8shelper.RegistryLoginFacade;

import java.util.List;

public interface DockerAware {
	
	List<RegistryLoginFacade> getRegistryLogins(String jobToken);
	
}

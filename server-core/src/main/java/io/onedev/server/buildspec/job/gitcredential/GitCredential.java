package io.cheeta.server.buildspec.job.gitcredential;

import com.google.common.collect.Lists;
import io.cheeta.k8shelper.CloneInfo;
import io.cheeta.server.Cheeta;
import io.cheeta.server.ServerConfig;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.ImplementationProvider;
import io.cheeta.server.model.Build;

import java.io.Serializable;
import java.util.Collection;

@Editable
@ImplementationProvider("getImplementations")
public interface GitCredential extends Serializable {
	
	CloneInfo newCloneInfo(Build build, String jobToken);
	
	@SuppressWarnings("unused")
	private static Collection<Class<? extends GitCredential>> getImplementations() {
		var implementations = Lists.newArrayList(DefaultCredential.class, HttpCredential.class);
		if (Cheeta.getInstance(ServerConfig.class).getSshPort() != 0)
			implementations.add(SshCredential.class);
		return implementations;
	}
	
}

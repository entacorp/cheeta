package io.cheeta.server.buildspec.job.gitcredential;

import io.cheeta.k8shelper.CloneInfo;
import io.cheeta.k8shelper.DefaultCloneInfo;
import io.cheeta.server.Cheeta;
import io.cheeta.server.web.UrlService;
import io.cheeta.server.model.Build;
import io.cheeta.server.annotation.Editable;

@Editable(name="Default", order=100)
public class DefaultCredential implements GitCredential {

	private static final long serialVersionUID = 1L;

	@Override
	public CloneInfo newCloneInfo(Build build, String jobToken) {
		return new DefaultCloneInfo(Cheeta.getInstance(UrlService.class).cloneUrlFor(build.getProject(), false), jobToken);
	}

}

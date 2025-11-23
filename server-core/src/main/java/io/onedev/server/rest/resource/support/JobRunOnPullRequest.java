package io.cheeta.server.rest.resource.support;

import javax.validation.constraints.NotNull;

import io.cheeta.server.model.Build;
import io.cheeta.server.rest.annotation.Api;
import io.cheeta.server.rest.annotation.EntityCreate;

@EntityCreate(Build.class)
public class JobRunOnPullRequest extends JobRun {
	
	private static final long serialVersionUID = 1L;

	@Api(order=100, description="Cheeta will build against merge preview commit of this pull request")
	private Long pullRequestId;
	
	@NotNull
	public Long getPullRequestId() {
		return pullRequestId;
	}

	public void setPullRequestId(Long pullRequestId) {
		this.pullRequestId = pullRequestId;
	}

}
package io.cheeta.server.buildspec.job.trigger;

import io.cheeta.server.buildspec.job.Job;
import io.cheeta.server.buildspec.job.TriggerMatch;
import io.cheeta.server.event.project.ProjectEvent;
import io.cheeta.server.event.project.pullrequest.PullRequestChanged;
import io.cheeta.server.model.support.pullrequest.changedata.PullRequestMergeData;
import io.cheeta.server.annotation.Editable;

@Editable(order=310, name="Pull request merge", description="Job will run on merge commit of target branch and source branch")
public class PullRequestMergeTrigger extends PullRequestTrigger {

	private static final long serialVersionUID = 1L;

	@Override
	protected TriggerMatch triggerMatches(ProjectEvent event, Job job) {
		if (event instanceof PullRequestChanged) {
			PullRequestChanged pullRequestChangeEvent = (PullRequestChanged) event;
			if (pullRequestChangeEvent.getChange().getData() instanceof PullRequestMergeData)
				return triggerMatches(pullRequestChangeEvent.getRequest(), "Pull request is merged");
		}
		return null;
	}

	@Override
	public String getTriggerDescription() {
		return getTriggerDescription("merge");
	}

}

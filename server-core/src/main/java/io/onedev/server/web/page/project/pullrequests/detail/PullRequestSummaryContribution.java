package io.cheeta.server.web.page.project.pullrequests.detail;

import java.util.List;

import io.cheeta.commons.loader.ExtensionPoint;
import io.cheeta.server.model.PullRequest;

@ExtensionPoint
public interface PullRequestSummaryContribution {

	List<PullRequestSummaryPart> getParts(PullRequest request);
	
	int getOrder();
	
}

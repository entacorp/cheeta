package io.cheeta.server.web.component.commandpalette;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.PullRequestService;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.search.entity.pullrequest.FuzzyCriteria;
import io.cheeta.server.search.entity.pullrequest.PullRequestQuery;
import io.cheeta.server.search.entity.pullrequest.PullRequestQueryParser;
import io.cheeta.server.search.entity.pullrequest.ReferenceCriteria;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.util.criteria.Criteria;
import io.cheeta.server.web.page.project.pullrequests.detail.PullRequestDetailPage;

public class PullRequestParam extends ParamSegment {

	private static final long serialVersionUID = 1L;
	
	public PullRequestParam(boolean optional) {
		super(PullRequestDetailPage.PARAM_REQUEST, optional);
	}

	@Override
	public Map<String, String> suggest(String matchWith, 
			Map<String, String> paramValues, int count) {
		Map<String, String> suggestions = new LinkedHashMap<>();
		Project project = ParsedUrl.getProject(paramValues);
		List<PullRequest> requests;
		PullRequestService pullRequestService = Cheeta.getInstance(PullRequestService.class);
		var subject = SecurityUtils.getSubject();
		if (matchWith.length() == 0) {
			requests = pullRequestService.query(subject, project, new PullRequestQuery(), false, 0, count);
		} else {
			Criteria<PullRequest> criteria;
			try {
				criteria = new ReferenceCriteria(project, matchWith, PullRequestQueryParser.Is);
			} catch (Exception e) {
				criteria = new FuzzyCriteria(matchWith);	
			}
			requests = pullRequestService.query(subject, project, new PullRequestQuery(criteria), false, 0, count);
		}
		for (PullRequest request: requests) 
			suggestions.put(request.getSummary(project), String.valueOf(request.getNumber()));
		return suggestions;
	}

	@Override
	public boolean isExactMatch(String matchWith, Map<String, String> paramValues) {
		PullRequestService pullRequestService = Cheeta.getInstance(PullRequestService.class);
		try {
			Long requestNumber = Long.valueOf(matchWith);
			if (pullRequestService.find(ParsedUrl.getProject(paramValues), requestNumber) != null) 
				return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}

}

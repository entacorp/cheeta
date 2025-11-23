package io.cheeta.server.web.util;

import org.jspecify.annotations.Nullable;

import io.cheeta.server.search.entity.issue.IssueQuery;

public interface IssueQueryAware {

	@Nullable
	IssueQuery getIssueQuery();
	
}

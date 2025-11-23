package io.cheeta.server.service;

import java.util.Date;
import java.util.Map;

import org.apache.shiro.subject.Subject;
import org.jetbrains.annotations.Nullable;

import io.cheeta.server.model.Issue;
import io.cheeta.server.model.IssueStateHistory;
import io.cheeta.server.util.ProjectScope;
import io.cheeta.server.util.criteria.Criteria;
import io.cheeta.server.web.util.StatsGroup;

public interface IssueStateHistoryService extends EntityService<IssueStateHistory> {
	
	Map<Integer, Map<String, Integer>> queryDurationStats(
			Subject subject, ProjectScope projectScope, @Nullable Criteria<Issue> issueFilter,  
			@Nullable Date startDate, @Nullable Date endDate, StatsGroup statsGroup);

	Map<Integer, Map<String, Integer>> queryFrequencyStats(
			Subject subject, ProjectScope projectScope, @Nullable Criteria<Issue> issueFilter, 
			@Nullable Date startDate, @Nullable Date endDate, StatsGroup statsGroup);
	
	Map<Integer, Map<String, Integer>> queryTrendStats(
			Subject subject, ProjectScope projectScope, @Nullable Criteria<Issue> issueFilter, 
			@Nullable Date startDate, @Nullable Date endDate, StatsGroup statsGroup);

}
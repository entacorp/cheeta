package io.cheeta.server.search.entity.issue;

import static io.cheeta.server.model.Issue.NAME_ESTIMATED_TIME;
import static io.cheeta.server.model.Issue.PROP_TOTAL_ESTIMATED_TIME;

import org.jspecify.annotations.Nullable;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.model.Issue;
import io.cheeta.server.util.ProjectScope;
import io.cheeta.server.util.criteria.Criteria;


public class EstimatedTimeCriteria extends Criteria<Issue> {

	private static final long serialVersionUID = 1L;

	private final int value;
	
	private final int operator;
	
	public EstimatedTimeCriteria(int value, int operator) {
		this.value = value;
		this.operator = operator;
	}

	@Override
	public Predicate getPredicate(@Nullable ProjectScope projectScope, CriteriaQuery<?> query, From<Issue, Issue> from, CriteriaBuilder builder) {
		Path<Integer> estimatedTimeAttribute = from.get(PROP_TOTAL_ESTIMATED_TIME);
		if (operator == IssueQueryLexer.Is)
			return builder.equal(estimatedTimeAttribute, value);
		else if (operator == IssueQueryLexer.IsNot)
			return builder.not(builder.equal(estimatedTimeAttribute, value));
		else if (operator == IssueQueryLexer.IsGreaterThan)
			return builder.greaterThan(estimatedTimeAttribute, value);
		else
			return builder.lessThan(estimatedTimeAttribute, value);
	}

	@Override
	public boolean matches(Issue issue) {
		if (operator == IssueQueryLexer.Is)
			return issue.getTotalEstimatedTime() == value;
		else if (operator == IssueQueryLexer.IsNot)
			return issue.getTotalEstimatedTime() != value;
		else if (operator == IssueQueryLexer.IsGreaterThan)
			return issue.getTotalEstimatedTime() > value;
		else
			return issue.getTotalEstimatedTime() < value;
	}

	@Override
	public String toStringWithoutParens() {
		var timeTrackingSetting = Cheeta.getInstance(SettingService.class).getIssueSetting().getTimeTrackingSetting();
		return quote(NAME_ESTIMATED_TIME) + " "
				+ IssueQuery.getRuleName(operator) + " "
				+ quote(timeTrackingSetting.formatWorkingPeriod(value, false));
	}

	@Override
	public void fill(Issue issue) {
	}

}

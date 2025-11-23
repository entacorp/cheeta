package io.cheeta.server.model.support.issue.transitionspec;

import java.util.ArrayList;
import java.util.List;

import io.cheeta.commons.codeassist.InputSuggestion;
import io.cheeta.server.model.Project;
import io.cheeta.server.search.entity.issue.IssueQueryLexer;
import io.cheeta.server.util.patternset.PatternSet;
import io.cheeta.server.util.usage.Usage;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.IssueQuery;
import io.cheeta.server.annotation.Patterns;
import io.cheeta.server.web.util.SuggestionUtils;

public abstract class PullRequestSpec extends AutoSpec {

	private static final long serialVersionUID = 1L;
	
	private String branches;
	
	public PullRequestSpec() {
		setIssueQuery(io.cheeta.server.search.entity.issue.IssueQuery
				.getRuleName(IssueQueryLexer.FixedInCurrentPullRequest));		
	}
	
	@Editable(name="Target Branches", order=100, placeholder="Any branch", description="Optionally specify "
			+ "space-separated target branches of the pull requests to check. Use '**', '*' or '?' for "
			+ "<a href='https://docs.cheeta.io/appendix/path-wildcard' target='_blank'>path wildcard match</a>. "
			+ "Prefix with '-' to exclude. Leave empty to match all branches")
	@Patterns(suggester = "suggestBranches", path=true)
	public String getBranches() {
		return branches;
	}

	public void setBranches(String branches) {
		this.branches = branches;
	}

	@Editable(order=9900, name="Applicable Issues", placeholder="All", description="Optionally specify issues "
			+ "applicable for this transition. Leave empty for all issues")
	@IssueQuery(withOrder = false, withCurrentPullRequestCriteria = true)
	@Override
	public String getIssueQuery() {
		return super.getIssueQuery();
	}

	public void setIssueQuery(String issueQuery) {
		super.setIssueQuery(issueQuery);
	}

	@SuppressWarnings("unused")
	private static List<InputSuggestion> suggestBranches(String matchWith) {
		if (Project.get() != null)
			return SuggestionUtils.suggestBranches(Project.get(), matchWith);
		else
			return new ArrayList<>();
	}
	
	@Override
	public Usage onDeleteBranch(String branchName) {
		Usage usage = super.onDeleteBranch(branchName);
		PatternSet patternSet = PatternSet.parse(branches);
		if (patternSet.getIncludes().contains(branchName) || patternSet.getExcludes().contains(branchName))
			usage.add("target branches");
		return usage;
	}
	
}

package io.cheeta.server.web.component.issue.choice;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.Nullable;

import org.hibernate.Hibernate;
import org.json.JSONException;
import org.json.JSONWriter;
import org.unbescape.html.HtmlEscape;

import com.google.common.collect.Lists;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.IssueService;
import io.cheeta.server.model.Issue;
import io.cheeta.server.model.Project;
import io.cheeta.server.search.entity.EntitySort;
import io.cheeta.server.search.entity.issue.FuzzyCriteria;
import io.cheeta.server.search.entity.issue.IssueQuery;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.util.ProjectScope;
import io.cheeta.server.util.ProjectScopedQuery;
import io.cheeta.server.util.criteria.AndCriteria;
import io.cheeta.server.util.criteria.Criteria;
import io.cheeta.server.web.WebConstants;
import io.cheeta.server.web.asset.emoji.Emojis;
import io.cheeta.server.web.component.select2.ChoiceProvider;
import io.cheeta.server.web.component.select2.Response;
import io.cheeta.server.web.component.select2.ResponseFiller;

public abstract class IssueChoiceProvider extends ChoiceProvider<Issue> {

	private static final long serialVersionUID = 1L;
	
	@Nullable
	protected abstract Project getProject();
	
	@Override
	public void toJson(Issue choice, JSONWriter writer) throws JSONException {
		writer
			.key("id").value(choice.getId())
			.key("reference").value(choice.getReference().toString(getProject()))
			.key("title").value(Emojis.getInstance().apply(HtmlEscape.escapeHtml5(choice.getTitle())));
	}

	@Override
	public Collection<Issue> toChoices(Collection<String> ids) {
		List<Issue> issues = Lists.newArrayList();
		IssueService issueService = Cheeta.getInstance(IssueService.class);
		for (String id: ids) {
			Issue issue = issueService.load(Long.valueOf(id)); 
			Hibernate.initialize(issue);
			issues.add(issue);
		}
		return issues;
	}
	
	@Nullable
	protected IssueQuery getBaseQuery() {
		return null;
	}
	
	@Override
	public void query(String term, int page, Response<Issue> response) {
		var count = (page+1) * WebConstants.PAGE_SIZE + 1;
		var scopedQuery = ProjectScopedQuery.of(getProject(), term, '#', '-');
		if (scopedQuery != null) {
			var projectScope = new ProjectScope(scopedQuery.getProject(), false, false);
			List<Criteria<Issue>> criterias = Lists.newArrayList(new FuzzyCriteria(scopedQuery.getQuery()));
			var sorts = new ArrayList<EntitySort>();
			if (getBaseQuery() != null) {
				if (getBaseQuery().getCriteria() != null)				
					criterias.add(getBaseQuery().getCriteria());
				sorts.addAll(getBaseQuery().getSorts());
			}
			var issueQuery = new IssueQuery(new AndCriteria<>(criterias), sorts);
			var subject = SecurityUtils.getSubject();
			var issues = Cheeta.getInstance(IssueService.class)
					.query(subject, projectScope, issueQuery, false, 0, count);
			new ResponseFiller<>(response).fill(issues, page, WebConstants.PAGE_SIZE);
		} else {
			response.setHasMore(false);
		}
	}
	
}
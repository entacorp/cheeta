package io.cheeta.server.web.component.pullrequest.choice;

import java.util.Collection;
import java.util.List;

import org.jspecify.annotations.Nullable;

import org.hibernate.Hibernate;
import org.json.JSONException;
import org.json.JSONWriter;
import org.unbescape.html.HtmlEscape;

import com.google.common.collect.Lists;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.PullRequestService;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.search.entity.pullrequest.FuzzyCriteria;
import io.cheeta.server.search.entity.pullrequest.PullRequestQuery;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.util.ProjectScopedQuery;
import io.cheeta.server.web.WebConstants;
import io.cheeta.server.web.asset.emoji.Emojis;
import io.cheeta.server.web.component.select2.ChoiceProvider;
import io.cheeta.server.web.component.select2.Response;
import io.cheeta.server.web.component.select2.ResponseFiller;

public abstract class PullRequestChoiceProvider extends ChoiceProvider<PullRequest> {

	private static final long serialVersionUID = 1L;
	
	@Override
	public void toJson(PullRequest choice, JSONWriter writer) throws JSONException {
		writer
			.key("id").value(choice.getId())
			.key("reference").value(choice.getReference().toString(getProject()))
			.key("title").value(Emojis.getInstance().apply(HtmlEscape.escapeHtml5(choice.getTitle())));
	}

	@Override
	public Collection<PullRequest> toChoices(Collection<String> ids) {
		List<PullRequest> requests = Lists.newArrayList();
		PullRequestService pullRequestService = Cheeta.getInstance(PullRequestService.class);
		for (String id: ids) {
			PullRequest request = pullRequestService.load(Long.valueOf(id)); 
			Hibernate.initialize(request);
			requests.add(request);
		}
		return requests;
	}

	@Override
	public void query(String term, int page, Response<PullRequest> response) {
		int count = (page+1) * WebConstants.PAGE_SIZE + 1;
		var scopedQuery = ProjectScopedQuery.of(getProject(), term, '#', '-');
		if (scopedQuery != null) {
			var subject = SecurityUtils.getSubject();
			List<PullRequest> requests = Cheeta.getInstance(PullRequestService.class)
					.query(subject, scopedQuery.getProject(), new PullRequestQuery(new FuzzyCriteria(scopedQuery.getQuery())), false, 0, count);
			new ResponseFiller<>(response).fill(requests, page, WebConstants.PAGE_SIZE);
		} else {
			response.setHasMore(false);
		}
	}
	
	@Nullable
	protected abstract Project getProject();
	
}
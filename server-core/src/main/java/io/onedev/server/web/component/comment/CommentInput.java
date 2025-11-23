package io.cheeta.server.web.component.comment;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.model.IModel;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.BuildService;
import io.cheeta.server.service.IssueService;
import io.cheeta.server.service.PullRequestService;
import io.cheeta.server.service.UserService;
import io.cheeta.server.model.Build;
import io.cheeta.server.model.Issue;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.User;
import io.cheeta.server.search.entity.issue.IssueQuery;
import io.cheeta.server.search.entity.pullrequest.PullRequestQuery;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.util.ProjectScope;
import io.cheeta.server.util.Similarities;
import io.cheeta.server.web.component.markdown.AtWhoReferenceSupport;
import io.cheeta.server.web.component.markdown.MarkdownEditor;
import io.cheeta.server.web.component.markdown.UserMentionSupport;

public abstract class CommentInput extends MarkdownEditor {

	public CommentInput(String id, IModel<String> model, boolean compactMode) {
		super(id, model, compactMode, null);
	}

	@Override
	protected final UserMentionSupport getUserMentionSupport() {
		return (query, count) -> {
			var cache = getUserService().cloneCache();
			var participants = getParticipants();
			var otherUsers = new ArrayList<>(cache.getUsers());
			otherUsers.removeAll(participants);
			
			var similarities = new Similarities<>(participants) {

				@Override
				public double getSimilarScore(User object) {
					return cache.getSimilarScore(object, query);
				}

			};
			if (similarities.size() < count) {
				similarities.addAll(new Similarities<>(otherUsers) {

					@Override
					public double getSimilarScore(User object) {
						return cache.getSimilarScore(object, query);
					}

				});
			}
			
			if (similarities.size() > count)
				return similarities.subList(0, count);
			else 
				return similarities;
		};
	}
	
	private UserService getUserService() {
		return Cheeta.getInstance(UserService.class);
	}
	
	protected List<User> getParticipants() {
		return new ArrayList<>();
	}
	
	@Override
	protected final AtWhoReferenceSupport getReferenceSupport() {
		return new AtWhoReferenceSupport() {

			@Override
			public Project getCurrentProject() {
				return getProject();
			}

			@Override
			public List<PullRequest> queryPullRequests(Project project, String query, int count) {
				var subject = SecurityUtils.getSubject();
				if (SecurityUtils.canReadCode(subject, project)) {
					var requestQuery = new PullRequestQuery(new io.cheeta.server.search.entity.pullrequest.FuzzyCriteria(query));
					return Cheeta.getInstance(PullRequestService.class).query(subject, project, requestQuery, false, 0, count);
				} else {
					return new ArrayList<>();
				}
			}

			@Override
			public List<Issue> queryIssues(Project project, String query, int count) {
				var subject = SecurityUtils.getSubject();
				if (SecurityUtils.canAccessProject(subject, project)) {
					var projectScope = new ProjectScope(project, false, false);
					var issueQuery = new IssueQuery(new io.cheeta.server.search.entity.issue.FuzzyCriteria(query));
					return Cheeta.getInstance(IssueService.class).query(subject, projectScope, issueQuery, false, 0, count);
				} else {
					return new ArrayList<>();
				}
			}

			@Override
			public List<Build> queryBuilds(Project project, String query, int count) {
				return Cheeta.getInstance(BuildService.class).query(SecurityUtils.getSubject(), project, query, count);
			}
			
		};
	}
	
	protected abstract Project getProject();
	
}

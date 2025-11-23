package io.cheeta.server.web.page.project.blob.render.renderers.markdown;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.form.FormComponentPanel;
import org.apache.wicket.model.Model;
import org.jetbrains.annotations.Nullable;

import com.google.common.collect.Sets;

import io.cheeta.commons.utils.StringUtils;
import io.cheeta.server.Cheeta;
import io.cheeta.server.service.BuildService;
import io.cheeta.server.service.IssueService;
import io.cheeta.server.service.PullRequestService;
import io.cheeta.server.service.UserService;
import io.cheeta.server.markdown.MarkdownService;
import io.cheeta.server.model.Build;
import io.cheeta.server.model.Issue;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.User;
import io.cheeta.server.search.entity.issue.IssueQuery;
import io.cheeta.server.search.entity.pullrequest.PullRequestQuery;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.util.ContentDetector;
import io.cheeta.server.util.ProjectScope;
import io.cheeta.server.util.Similarities;
import io.cheeta.server.util.facade.UserCache;
import io.cheeta.server.web.component.markdown.AtWhoReferenceSupport;
import io.cheeta.server.web.component.markdown.MarkdownEditor;
import io.cheeta.server.web.component.markdown.UserMentionSupport;
import io.cheeta.server.web.page.project.blob.render.BlobRenderContext;
import io.cheeta.server.web.page.project.blob.render.BlobRenderContext.Mode;

class MarkdownBlobEditor extends FormComponentPanel<byte[]> {

	private final BlobRenderContext context;
	
	private MarkdownEditor input;
	
	public MarkdownBlobEditor(String id, BlobRenderContext context, byte[] initialContent) {
		super(id, Model.of(initialContent));

		this.context = context;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		Charset detectedCharset = ContentDetector.detectCharset(getModelObject());
		Charset charset = detectedCharset!=null?detectedCharset:Charset.defaultCharset();
		add(input = new MarkdownEditor("input", Model.of(new String(getModelObject(), charset)), 
				false, context) {

			@Override
			protected String renderMarkdown(String markdown) {
				MarkdownService manager = Cheeta.getInstance(MarkdownService.class);
				return manager.process(manager.render(markdown), context.getProject(), context, null, false);
			}

			@Nullable
			@Override
			protected String getAutosaveKey() {
				return "project:" + getBlobRenderContext().getProject().getId() + ":markdown-file"; 
			}
			
			@Override
			protected boolean shouldTrimInput() {
				return false;
			}

			@Override
			protected UserMentionSupport getUserMentionSupport() {
				return new UserMentionSupport() {

					@Override
					public List<User> findUsers(String query, int count) {
						UserCache cache = Cheeta.getInstance(UserService.class).cloneCache();
						List<User> users = new ArrayList<>(cache.getUsers());
						users.sort(cache.comparingDisplayName(Sets.newHashSet()));
						
						users = new Similarities<User>(users) {

							@Override
							public double getSimilarScore(User object) {
								return cache.getSimilarScore(object, query);
							}
							
						};
						
						if (users.size() > count)
							return users.subList(0, count);
						else
							return users;
					}
					
				};
			}
			
			@Override
			protected AtWhoReferenceSupport getReferenceSupport() {
				return new AtWhoReferenceSupport() {

					@Override
					public Project getCurrentProject() {
						return context.getProject();
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
						var subject = SecurityUtils.getSubject();
						return Cheeta.getInstance(BuildService.class).query(subject, project, query, count);
					}
					
				};
			}

		});
		
		if (context.getMode() != Mode.EDIT)
			input.add(AttributeAppender.append("class", "no-autofocus"));
		input.setOutputMarkupId(true);
	}

	@Override
	public void convertInput() {
		String content = input.getConvertedInput();
		if (content != null) {
			/*
			 * Textarea always uses CRLF as line ending, and below we change back to original EOL
			 */
			String initialContent = input.getModelObject();
			if (initialContent == null || !initialContent.contains("\r\n"))
				content = StringUtils.replace(content, "\r\n", "\n");
			setConvertedInput(content.getBytes(StandardCharsets.UTF_8));
		} else {
			setConvertedInput(new byte[0]);
		}
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		if (context.getMode() == Mode.EDIT) {
			String script = String.format("$('#%s textarea').focus();", input.getMarkupId());
			response.render(OnDomReadyHeaderItem.forScript(script));
		}
	}

}

package io.cheeta.server.web.page.project.pullrequests.detail.activities.activity;

import static io.cheeta.server.web.translation.Translation._T;

import java.util.Collection;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.jetbrains.annotations.Nullable;

import io.cheeta.commons.utils.ExplicitException;
import io.cheeta.server.Cheeta;
import io.cheeta.server.attachment.AttachmentSupport;
import io.cheeta.server.attachment.ProjectAttachmentSupport;
import io.cheeta.server.service.PullRequestCommentService;
import io.cheeta.server.service.PullRequestCommentReactionService;
import io.cheeta.server.service.PullRequestCommentRevisionService;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.PullRequestComment;
import io.cheeta.server.model.PullRequestCommentRevision;
import io.cheeta.server.model.User;
import io.cheeta.server.model.support.CommentRevision;
import io.cheeta.server.model.support.EntityReaction;
import io.cheeta.server.persistence.TransactionService;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.util.DateUtils;
import io.cheeta.server.web.component.comment.CommentHistoryLink;
import io.cheeta.server.web.component.comment.CommentPanel;
import io.cheeta.server.web.component.comment.ReactionSupport;
import io.cheeta.server.web.component.markdown.ContentVersionSupport;
import io.cheeta.server.web.component.user.ident.Mode;
import io.cheeta.server.web.component.user.ident.UserIdentPanel;
import io.cheeta.server.web.page.base.BasePage;
import io.cheeta.server.web.page.project.pullrequests.detail.activities.SinceChangesLink;
import io.cheeta.server.web.util.DeleteCallback;

class PullRequestCommentPanel extends Panel {
	
	public PullRequestCommentPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		add(new UserIdentPanel("avatar", getComment().getUser(), Mode.AVATAR));
		add(new UserIdentPanel("name", getComment().getUser(), Mode.NAME));
		add(new Label("age", DateUtils.formatAge(getComment().getDate()))
			.add(new AttributeAppender("title", DateUtils.formatDateTime(getComment().getDate()))));
		
		add(new SinceChangesLink("changes", new AbstractReadOnlyModel<PullRequest>() {

			@Override
			public PullRequest getObject() {
				return getComment().getRequest();
			}

		}, getComment().getDate()));
		
		add(new WebMarkupContainer("anchor") {

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				tag.put("href", "#" + getComment().getAnchor());
			}
			
		});
		
		add(new CommentPanel("body") {

			@Override
			protected String getComment() {
				return PullRequestCommentPanel.this.getComment().getContent();
			}

			@Override
			protected void onSaveComment(AjaxRequestTarget target, String comment) {
				if (comment.length() > PullRequestComment.MAX_CONTENT_LEN)
					throw new ExplicitException("Comment too long");
				var entity = PullRequestCommentPanel.this.getComment();

				var oldComment = entity.getContent();
				if (!oldComment.equals(comment)) {
					getTransactionService().run(() -> {
						entity.setContent(comment);
						entity.setRevisionCount(entity.getRevisionCount() + 1);
						getPullRequestCommentService().update(entity);

						var revision = new PullRequestCommentRevision();
						revision.setComment(entity);
						revision.setUser(SecurityUtils.getUser());
						revision.setOldContent(oldComment);
						revision.setNewContent(comment);
						getPullRequestCommentRevisionService().create(revision);
					});
					var page = (BasePage) getPage();
					page.notifyObservableChange(target, PullRequest.getChangeObservable(entity.getRequest().getId()));				
				}
			}

			@Override
			protected Project getProject() {
				return PullRequestCommentPanel.this.getComment().getProject();
			}

			@Nullable
			@Override
			protected String getAutosaveKey() {
				return "pull-request-comment:" + PullRequestCommentPanel.this.getComment().getId();
			}

			@Override
			protected AttachmentSupport getAttachmentSupport() {
				return new ProjectAttachmentSupport(getProject(), 
						PullRequestCommentPanel.this.getComment().getRequest().getUUID(), 
						SecurityUtils.canManagePullRequests(getProject()));
			}

			@Override
			protected List<User> getParticipants() {
				return PullRequestCommentPanel.this.getComment().getRequest().getParticipants();
			}
			
			@Override
			protected boolean canManageComment() {
				return SecurityUtils.canModifyOrDelete(PullRequestCommentPanel.this.getComment());
			}

			@Override
			protected String getRequiredLabel() {
				return _T("Comment");
			}

			@Override
			protected ContentVersionSupport getContentVersionSupport() {
				return () -> 0;
			}

			@Override
			protected DeleteCallback getDeleteCallback() {
				return target -> {
					var page = (BasePage) getPage();
					var pullRequest = PullRequestCommentPanel.this.getComment().getRequest();
					target.appendJavaScript(String.format("$('#%s').remove();", PullRequestCommentPanel.this.getMarkupId()));
					PullRequestCommentPanel.this.remove();
					getPullRequestCommentService().delete(PullRequestCommentPanel.this.getComment());
					page.notifyObservableChange(target, PullRequest.getChangeObservable(pullRequest.getId()));
				};
			}

			@Override
			protected ReactionSupport getReactionSupport() {
				return new ReactionSupport() {

					@Override
					public Collection<? extends EntityReaction> getReactions() {
						return PullRequestCommentPanel.this.getComment().getReactions();
					}
		
					@Override
					public void onToggleEmoji(AjaxRequestTarget target, String emoji) {
						getPullRequestCommentReactionService().toggleEmoji(
								SecurityUtils.getUser(), 
								PullRequestCommentPanel.this.getComment(), 
								emoji);
					}
							
				};
			}
			
			@Override
			protected Component newMoreActions(String id) {
				var fragment = new Fragment(id, "historyFrag", PullRequestCommentPanel.this);
				fragment.add(new CommentHistoryLink("history") {

					@Override
					protected Collection<? extends CommentRevision> getCommentRevisions() {
						return PullRequestCommentPanel.this.getComment().getRevisions();
					}

					@Override
					protected void onConfigure() {
						super.onConfigure();
						setVisible(PullRequestCommentPanel.this.getComment().getRevisionCount() != 0);
					}

				});
				return fragment;
			}

		});

		setMarkupId(getComment().getAnchor());
		setOutputMarkupId(true);
	}

	private TransactionService getTransactionService() {
		return Cheeta.getInstance(TransactionService.class);
	}

	private PullRequestCommentRevisionService getPullRequestCommentRevisionService() {
		return Cheeta.getInstance(PullRequestCommentRevisionService.class);
	}

	private PullRequestCommentService getPullRequestCommentService() {
		return Cheeta.getInstance(PullRequestCommentService.class);
	}

	private PullRequestCommentReactionService getPullRequestCommentReactionService() {
		return Cheeta.getInstance(PullRequestCommentReactionService.class);
	}

	private PullRequestComment getComment() {
		return ((PullRequestCommentActivity) getDefaultModelObject()).getComment();
	}

}

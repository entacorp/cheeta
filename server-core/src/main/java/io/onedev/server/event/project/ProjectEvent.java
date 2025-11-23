package io.cheeta.server.event.project;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.ProjectService;
import io.cheeta.server.service.UserService;
import io.cheeta.server.event.Event;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.User;
import io.cheeta.server.model.support.LastActivity;
import io.cheeta.server.notification.ActivityDetail;
import io.cheeta.server.util.commenttext.CommentText;
import io.cheeta.server.web.UrlService;

import org.jspecify.annotations.Nullable;
import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

public abstract class ProjectEvent extends Event implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private final Long projectId;
	
	private final Long userId;
	
	private final Date date;
	
	private transient Optional<CommentText> commentText;
	
	public ProjectEvent(@Nullable User user, Date date, Project project) {
		userId = User.idOf(user);
		this.date = date;
		projectId = project.getId();
	}
	
	public Project getProject() {
		return Cheeta.getInstance(ProjectService.class).load(projectId);
	}
	
	@Nullable
	public User getUser() {
		return userId != null? Cheeta.getInstance(UserService.class).load(userId): null;
	}

	public Date getDate() {
		return date;
	}
	
	public abstract String getActivity();
	
	@Nullable
	public final CommentText getCommentText() {
		if (commentText == null)
			commentText = Optional.ofNullable(newCommentText());
		return commentText.orElse(null);
	}
	
	@Nullable
	protected CommentText newCommentText() {
		return null;
	}
	
	@Nullable
	public ActivityDetail getActivityDetail() {
		return null;
	}
	
	public LastActivity getLastUpdate() {
		LastActivity lastActivity = new LastActivity();
		lastActivity.setUser(getUser());
		lastActivity.setDescription(getActivity());
		lastActivity.setDate(getDate());
		return lastActivity;
	}
	
	@Nullable
	public String getTextBody() {
		ActivityDetail activityDetail = getActivityDetail();
		CommentText commentText = getCommentText();
		
		if (activityDetail != null && commentText != null)
			return activityDetail.getTextVersion() + "\n\n" + commentText.getPlainContent();
		else if (activityDetail != null)
			return activityDetail.getTextVersion();
		else if (commentText != null)
			return commentText.getPlainContent();
		else
			return null;
	}
	
	@Nullable
	public String getHtmlBody() {
		ActivityDetail activityDetail = getActivityDetail();
		CommentText commentText = getCommentText();

		if (activityDetail != null && commentText != null)
			return activityDetail.getHtmlVersion() + "<br>" + commentText.getHtmlContent();
		else if (activityDetail != null)
			return activityDetail.getHtmlVersion();
		else if (commentText != null)
			return commentText.getHtmlContent();
		else
			return null;
	}
	
	public String getUrl() {
		return Cheeta.getInstance(UrlService.class).urlFor(getProject(), true);
	}
	
	@Nullable
	public String getLockName() {
		return null;
	}
	
	public boolean isMinor() {
		return false;
	}
	
}

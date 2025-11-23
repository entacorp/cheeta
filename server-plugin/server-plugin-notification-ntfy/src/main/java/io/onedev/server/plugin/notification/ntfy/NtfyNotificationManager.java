package io.cheeta.server.plugin.notification.ntfy;

import com.vladsch.flexmark.ast.Image;
import com.vladsch.flexmark.ast.Link;
import com.vladsch.flexmark.formatter.NodeFormattingHandler;
import io.cheeta.server.event.project.ProjectEvent;
import io.cheeta.server.markdown.ExternalLinkFormatter;
import io.cheeta.server.markdown.MarkdownService;
import io.cheeta.server.notification.ActivityDetail;
import io.cheeta.server.notification.ChannelNotificationManager;
import io.cheeta.server.util.commenttext.CommentText;
import io.cheeta.server.util.commenttext.MarkdownText;
import io.cheeta.server.util.commenttext.PlainText;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

import static java.nio.charset.StandardCharsets.*;
import static org.apache.http.entity.ContentType.create;

@Singleton
public class NtfyNotificationManager extends ChannelNotificationManager<NtfyNotificationSetting> {

	@Inject
	private MarkdownService markdownService;

	@Override
	protected void post(HttpPost post, String title, ProjectEvent event) {
		post.setHeader("Markdown", "yes");
		post.setHeader("Title", title);
		post.setHeader("Click", event.getUrl());

		var body = new StringBuilder();

		ActivityDetail activityDetail = event.getActivityDetail();
		if (activityDetail != null) 
			body.append(activityDetail.getTextVersion()).append("\n\n");

		CommentText commentText = event.getCommentText();
		if (commentText instanceof MarkdownText) {
			String markdown = commentText.getPlainContent();

			Set<NodeFormattingHandler<?>> handlers = new HashSet<>();
			handlers.add(new NodeFormattingHandler<>(Link.class, new ExternalLinkFormatter<>()));
			handlers.add(new NodeFormattingHandler<>(Image.class, new ExternalLinkFormatter<>()));

			body.append(markdownService.format(markdown, handlers));
		} else if (commentText instanceof PlainText) {
			body.append(commentText.getPlainContent());
		}
		
		post.setEntity(new StringEntity(body.toString(), create("text/markdown", UTF_8)));
	}
}

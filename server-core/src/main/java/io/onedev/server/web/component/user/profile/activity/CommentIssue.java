package io.cheeta.server.web.component.user.profile.activity;

import static io.cheeta.server.web.translation.Translation._T;

import java.text.MessageFormat;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.unbescape.html.HtmlEscape;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.IssueCommentService;
import io.cheeta.server.model.Issue;
import io.cheeta.server.model.IssueComment;
import io.cheeta.server.web.UrlService;

public class CommentIssue extends IssueActivity {

    private final Long commentId;

    public CommentIssue(IssueComment comment) {
        super(comment.getDate());
        this.commentId = comment.getId();
    }

    private IssueComment getComment() {
        return Cheeta.getInstance(IssueCommentService.class).load(commentId);
    }
    
    @Override
    public Issue getIssue() {
        return getComment().getIssue();
    }
    
    @Override
    public Component render(String id) {
        var comment = getComment();
        var url = Cheeta.getInstance(UrlService.class).urlFor(comment, false);
        var label = MessageFormat.format(_T("Commented on issue \"{0}\" ({1})"), "<a href=\"" + url + "\">" + comment.getIssue().getReference() + "</a>", HtmlEscape.escapeHtml5(comment.getIssue().getTitle()));
        return new Label(id, label).setEscapeModelStrings(false);
    }
}
package io.cheeta.server.web.component.user.profile.activity;

import static io.cheeta.server.web.translation.Translation._T;

import java.text.MessageFormat;
import java.util.Date;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.unbescape.html.HtmlEscape;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.CodeCommentService;
import io.cheeta.server.model.CodeComment;
import io.cheeta.server.web.UrlService;

public class UnresolveCodeComment extends CodeCommentActivity {

    private final Long commentId;

    public UnresolveCodeComment(Date date, CodeComment comment) {
        super(date);
        this.commentId = comment.getId();
    }

    @Override
    public CodeComment getComment() {
        return Cheeta.getInstance(CodeCommentService.class).load(commentId);
    }
    
    @Override
    public Component render(String id) {
        var comment = getComment();
        var url = Cheeta.getInstance(UrlService.class).urlFor(comment, false);
        var label = MessageFormat.format(_T("Unresolved comment on file \"{0}\" in project \"{1}\""), "<a href=\"" + url + "\">" + HtmlEscape.escapeHtml5(comment.getMark().getPath()) + "</a>", comment.getProject().getPath());
        return new Label(id, label).setEscapeModelStrings(false);
    }

}

package io.cheeta.server.web.component.user.profile.activity;

import static io.cheeta.server.web.translation.Translation._T;

import java.text.MessageFormat;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.unbescape.html.HtmlEscape;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.CodeCommentReplyService;
import io.cheeta.server.model.CodeComment;
import io.cheeta.server.model.CodeCommentReply;
import io.cheeta.server.web.UrlService;

public class ReplyCodeComment extends CodeCommentActivity {

    private final Long replyId;

    public ReplyCodeComment(CodeCommentReply reply) {
        super(reply.getDate());
        this.replyId = reply.getId();
    }

    @Override
    public CodeComment getComment() {
        return Cheeta.getInstance(CodeCommentReplyService.class).load(replyId).getComment();
    }
    
    @Override
    public Component render(String id) {
        var comment = getComment();
        var url = Cheeta.getInstance(UrlService.class).urlFor(comment, false);
        var label = MessageFormat.format(_T("Replied to comment on file \"{0}\" in project \"{1}\""), "<a href=\"" + url + "\">" + HtmlEscape.escapeHtml5(comment.getMark().getPath()) + "</a>", comment.getProject().getPath());
        return new Label(id, label).setEscapeModelStrings(false);
    }

}

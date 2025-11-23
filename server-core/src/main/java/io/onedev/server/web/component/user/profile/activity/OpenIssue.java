package io.cheeta.server.web.component.user.profile.activity;

import static io.cheeta.server.web.translation.Translation._T;

import java.text.MessageFormat;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.unbescape.html.HtmlEscape;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.IssueService;
import io.cheeta.server.model.Issue;
import io.cheeta.server.web.UrlService;

public class OpenIssue extends IssueActivity {

    private final Long issueId;

    public OpenIssue(Issue issue) {
        super(issue.getSubmitDate());
        this.issueId = issue.getId();
    }

    @Override
    public Issue getIssue() {
        return Cheeta.getInstance(IssueService.class).load(issueId);
    }
    
    @Override
    public Component render(String id) {
        var issue = getIssue();
        var url = Cheeta.getInstance(UrlService.class).urlFor(issue, false);
        var label = MessageFormat.format(_T("Opened issue \"{0}\" ({1})"), "<a href=\"" + url + "\">" + issue.getReference() + "</a>", HtmlEscape.escapeHtml5(issue.getTitle()));
        return new Label(id, label).setEscapeModelStrings(false);
    }

}
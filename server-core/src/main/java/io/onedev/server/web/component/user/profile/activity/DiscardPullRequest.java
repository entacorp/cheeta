package io.cheeta.server.web.component.user.profile.activity;

import static io.cheeta.server.web.translation.Translation._T;

import java.text.MessageFormat;
import java.util.Date;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.cycle.RequestCycle;
import org.unbescape.html.HtmlEscape;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.PullRequestService;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.web.page.project.pullrequests.detail.activities.PullRequestActivitiesPage;

public class DiscardPullRequest extends PullRequestActivity {

    private final Long requestId;

    public DiscardPullRequest(Date date, PullRequest pullRequest) {
        super(date);
        this.requestId = pullRequest.getId();
    }

    @Override
    public PullRequest getPullRequest() {
        return Cheeta.getInstance(PullRequestService.class).load(requestId);
    }
    
    @Override
    public Component render(String id) {
        var request = getPullRequest();
        var url = RequestCycle.get().urlFor(PullRequestActivitiesPage.class, PullRequestActivitiesPage.paramsOf(request));
        var label = MessageFormat.format(_T("Discarded pull request \"{0}\" ({1})"), "<a href=\"" + url + "\">" + request.getReference() + "</a>", HtmlEscape.escapeHtml5(request.getTitle()));
        return new Label(id, label).setEscapeModelStrings(false);
    }
    
}
package io.cheeta.server.web.page.project.pullrequests.detail.activities.activity;

import java.util.Date;

import org.apache.wicket.Component;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.PullRequestChangeService;
import io.cheeta.server.model.PullRequestChange;
import io.cheeta.server.web.page.project.pullrequests.detail.activities.PullRequestActivity;

public class PullRequestChangeActivity implements PullRequestActivity {

	private final Long changeId;
	
	public PullRequestChangeActivity(PullRequestChange change) {
		changeId = change.getId();
	}
	
	@Override
	public Component render(String panelId) {
		return new PullRequestChangePanel(panelId);
	}

	public PullRequestChange getChange() {
		return Cheeta.getInstance(PullRequestChangeService.class).load(changeId);
	}

	@Override
	public Date getDate() {
		return getChange().getDate();
	}

}

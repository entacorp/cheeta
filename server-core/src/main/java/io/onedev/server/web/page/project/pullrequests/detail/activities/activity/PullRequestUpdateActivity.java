package io.cheeta.server.web.page.project.pullrequests.detail.activities.activity;

import java.util.Date;

import org.apache.wicket.Component;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.PullRequestUpdateService;
import io.cheeta.server.model.PullRequestUpdate;
import io.cheeta.server.web.page.project.pullrequests.detail.activities.PullRequestActivity;

public class PullRequestUpdateActivity implements PullRequestActivity {

	private final Long updateId;
	
	public PullRequestUpdateActivity(PullRequestUpdate update) {
		updateId = update.getId();
	}
	
	@Override
	public Component render(String componentId) {
		return new PullRequestUpdatePanel(componentId);
	}

	public PullRequestUpdate getUpdate() {
		return Cheeta.getInstance(PullRequestUpdateService.class).load(updateId);
	}

	@Override
	public Date getDate() {
		return getUpdate().getDate();
	}

}

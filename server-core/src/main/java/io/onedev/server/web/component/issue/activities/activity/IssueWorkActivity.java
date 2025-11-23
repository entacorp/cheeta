package io.cheeta.server.web.component.issue.activities.activity;

import java.util.Date;

import org.apache.wicket.markup.html.panel.Panel;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.IssueWorkService;
import io.cheeta.server.model.IssueWork;

public class IssueWorkActivity implements IssueActivity {

	private final Long workId;
	
	public IssueWorkActivity(IssueWork work) {
		workId = work.getId();
	}
	
	@Override
	public Panel render(String panelId) {
		return new IssueWorkPanel(panelId);
	}
	
	public Long getWorkId() {
		return workId;
	}
	
	public IssueWork getWork() {
		return Cheeta.getInstance(IssueWorkService.class).load(workId);
	}
	
	@Override
	public Date getDate() {
		return getWork().getDate();
	}
	
}

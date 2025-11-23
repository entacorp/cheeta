package io.cheeta.server.web.component.issue.progress;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.IssueService;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.search.entity.issue.IssueQuery;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.util.IssueTimes;
import io.cheeta.server.util.ProjectScope;
import io.cheeta.server.web.behavior.CompletionRateBehavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;

import org.jspecify.annotations.Nullable;

public abstract class QueriedIssuesProgressPanel extends Panel {
	
	private final IModel<IssueTimes> timesModel = new LoadableDetachableModel<IssueTimes>() {
		@Override
		protected IssueTimes load() {
			var subject = SecurityUtils.getSubject();
			return Cheeta.getInstance(IssueService.class).queryTimes(subject, getProjectScope(), getQuery().getCriteria());
		}
	};
	
	public QueriedIssuesProgressPanel(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		if (getQuery() != null) {
			var fragment = new Fragment("content", "hasQueryFrag", this);
			fragment.add(new WebMarkupContainer("completion").add(new CompletionRateBehavior() {
				@Override
				protected long getTotal() {
					return timesModel.getObject().getEstimatedTime();
				}

				@Override
				protected long getCompleted() {
					return timesModel.getObject().getSpentTime();
				}
			}));

			var timeTrackingSetting = Cheeta.getInstance(SettingService.class).getIssueSetting().getTimeTrackingSetting();
			fragment.add(new Label("estimatedTime", timeTrackingSetting.formatWorkingPeriod(timesModel.getObject().getEstimatedTime(), true)));
			fragment.add(new Label("spentTime", timeTrackingSetting.formatWorkingPeriod(timesModel.getObject().getSpentTime(), true)));
			add(fragment);
		} else {
			add(new Fragment("content", "noQueryFrag", this));
		}
	}

	@Override
	protected void onDetach() {
		timesModel.detach();
		super.onDetach();
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(JavaScriptHeaderItem.forReference(new ProgressResourceReference()));
	}

	protected abstract ProjectScope getProjectScope();
	
	@Nullable
	protected abstract IssueQuery getQuery();
}

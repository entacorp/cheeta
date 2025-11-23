package io.cheeta.server.web.component.iteration.burndown;

import static io.cheeta.server.web.translation.Translation._T;
import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;

import io.cheeta.server.Cheeta;
import io.cheeta.server.buildspecmodel.inputspec.InputSpec;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.support.administration.GlobalIssueSetting;
import io.cheeta.server.model.support.issue.field.spec.FieldSpec;
import io.cheeta.server.web.util.WicketUtils;

public class BurndownIndicators {

	public static final String ISSUE_COUNT = "Issue Count";

	public static final String REMAINING_TIME = "Remaining Time";

	public static final String ESTIMATED_TIME = "Estimated Time";

	private static GlobalIssueSetting getIssueSetting() {
		return Cheeta.getInstance(SettingService.class).getIssueSetting();
	}
	
	public static List<String> getChoices(Project project) {
		var choices = new ArrayList<String>();
		if (project.isTimeTracking() && WicketUtils.isSubscriptionActive()) {
			choices.add(REMAINING_TIME);
			choices.add(ESTIMATED_TIME);
		}
		choices.add(ISSUE_COUNT);

		choices.addAll(getIssueSetting().getFieldSpecs().stream()
				.filter(it -> it.getType().equals(InputSpec.INTEGER))
				.map(FieldSpec::getName).collect(toList()));

		return choices;
	}

	public static String getDefault(Project project) {
		if (project.isTimeTracking() && WicketUtils.isSubscriptionActive())
			return REMAINING_TIME;
		else
			return ISSUE_COUNT;
	}
	
	public static String getDisplayName(String indicator) {
		if (indicator.equals(REMAINING_TIME)) {
			return _T("Remaining time");
		} else if (indicator.equals(ESTIMATED_TIME)) {
			return _T("Estimated time");
		} else if (indicator.equals(ISSUE_COUNT)) {
			return _T("Issue count");
		} else {
			return indicator;
		}
	}
	
}

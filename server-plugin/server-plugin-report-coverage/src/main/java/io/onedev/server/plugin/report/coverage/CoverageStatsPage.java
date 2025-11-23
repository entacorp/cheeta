package io.cheeta.server.plugin.report.coverage;

import static io.cheeta.server.web.translation.Translation._T;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import io.cheeta.server.model.CoverageMetric;
import io.cheeta.server.web.page.project.stats.buildmetric.BuildMetricStatsPage;

public class CoverageStatsPage extends BuildMetricStatsPage<CoverageMetric> {

	public CoverageStatsPage(PageParameters params) {
		super(params);
	}

	@Override
	protected Component newProjectTitle(String componentId) {
		return new Label(componentId, _T("Coverage Statistics"));
	}

}

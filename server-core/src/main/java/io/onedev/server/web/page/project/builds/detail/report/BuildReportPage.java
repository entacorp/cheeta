package io.cheeta.server.web.page.project.builds.detail.report;

import io.cheeta.commons.utils.ExplicitException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import io.cheeta.server.model.Build;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.web.page.project.builds.detail.BuildDetailPage;

public abstract class BuildReportPage extends BuildDetailPage {

	private static final String PARAM_REPORT = "report";

	private final String reportName;
	
	public BuildReportPage(PageParameters params) {
		super(params);
		reportName = params.get(PARAM_REPORT).toString();
		if (reportName.contains(".."))
			throw new ExplicitException("Invalid request path");
	}
	
	@Override
	protected boolean isPermitted() {
		return SecurityUtils.canAccessReport(getBuild(), reportName);
	}
	
	public String getReportName() {
		return reportName;
	}

	public static PageParameters paramsOf(Build build, String reportName) {
		PageParameters params = paramsOf(build);
		params.add(PARAM_REPORT, reportName);
		return params;
	}
	
}

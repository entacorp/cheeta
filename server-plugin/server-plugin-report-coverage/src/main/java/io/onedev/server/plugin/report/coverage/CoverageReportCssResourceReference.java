package io.cheeta.server.plugin.report.coverage;

import io.cheeta.server.web.page.base.BaseDependentCssResourceReference;

public class CoverageReportCssResourceReference extends BaseDependentCssResourceReference {

	private static final long serialVersionUID = 1L;

	public CoverageReportCssResourceReference() {
		super(CoverageReportCssResourceReference.class, "coverage-report.css");
	}

}

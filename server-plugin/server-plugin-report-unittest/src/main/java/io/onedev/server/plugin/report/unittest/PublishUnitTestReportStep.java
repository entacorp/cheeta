package io.cheeta.server.plugin.report.unittest;

import io.cheeta.commons.utils.FileUtils;
import io.cheeta.commons.utils.TaskLogger;
import io.cheeta.k8shelper.ServerStepResult;
import io.cheeta.server.Cheeta;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.buildspec.step.PublishReportStep;
import io.cheeta.server.service.BuildService;
import io.cheeta.server.service.BuildMetricService;
import io.cheeta.server.service.ProjectService;
import io.cheeta.server.model.Build;
import io.cheeta.server.model.UnitTestMetric;
import io.cheeta.server.persistence.SessionService;
import io.cheeta.server.persistence.dao.Dao;

import org.jspecify.annotations.Nullable;
import java.io.File;

import static io.cheeta.commons.utils.LockUtils.write;
import static io.cheeta.server.plugin.report.unittest.UnitTestReport.getReportLockName;

@Editable
public abstract class PublishUnitTestReportStep extends PublishReportStep {

	private static final long serialVersionUID = 1L;
	
	@Override
	public ServerStepResult run(Long buildId, File inputDir, TaskLogger logger) {
		Cheeta.getInstance(SessionService.class).run(() -> {
			var build = Cheeta.getInstance(BuildService.class).load(buildId);
			File reportDir = new File(build.getDir(), UnitTestReport.CATEGORY + "/" + getReportName());

			UnitTestReport report = write(getReportLockName(build), () -> {
				UnitTestReport aReport = process(build, inputDir, logger);
				if (aReport != null) {
					FileUtils.createDir(reportDir);
					aReport.writeTo(reportDir);
					Cheeta.getInstance(ProjectService.class).directoryModified(
							build.getProject().getId(), reportDir.getParentFile());
					return aReport;
				} else {
					return null;
				}
			});

			if (report != null) {
				var metric = Cheeta.getInstance(BuildMetricService.class).find(UnitTestMetric.class, build, getReportName());
				if (metric == null) {
					metric = new UnitTestMetric();
					metric.setBuild(build);
					metric.setReportName(getReportName());
				}
				metric.setTestCaseSuccessRate(report.getTestCaseSuccessRate());
				metric.setTestSuiteSuccessRate(report.getTestSuiteSuccessRate());
				metric.setNumOfTestCases(report.getTestCases().size());
				metric.setNumOfTestSuites(report.getTestSuites().size());
				metric.setTotalTestDuration(report.getTestDuration());
				Cheeta.getInstance(Dao.class).persist(metric);
			}

		});
		return new ServerStepResult(true);
	}

	@Nullable
	protected abstract UnitTestReport process(Build build, File inputDir, TaskLogger logger);
	
}

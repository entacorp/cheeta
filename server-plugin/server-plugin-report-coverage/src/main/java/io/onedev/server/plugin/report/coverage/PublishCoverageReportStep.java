package io.cheeta.server.plugin.report.coverage;

import io.cheeta.commons.utils.ExceptionUtils;
import io.cheeta.commons.utils.FileUtils;
import io.cheeta.commons.utils.TaskLogger;
import io.cheeta.k8shelper.ServerStepResult;
import io.cheeta.server.Cheeta;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.buildspec.step.PublishReportStep;
import io.cheeta.server.codequality.CoverageStatus;
import io.cheeta.server.service.BuildService;
import io.cheeta.server.service.BuildMetricService;
import io.cheeta.server.service.ProjectService;
import io.cheeta.server.model.Build;
import io.cheeta.server.model.CoverageMetric;
import io.cheeta.server.persistence.SessionService;
import io.cheeta.server.persistence.dao.Dao;
import org.apache.commons.lang.SerializationUtils;

import org.jspecify.annotations.Nullable;
import java.io.*;
import java.util.Map;

import static io.cheeta.commons.utils.LockUtils.write;
import static io.cheeta.server.plugin.report.coverage.CoverageStats.getReportLockName;

@Editable
public abstract class PublishCoverageReportStep extends PublishReportStep {

	private static final long serialVersionUID = 1L;
	
	@Override
	public ServerStepResult run(Long buildId, File inputDir, TaskLogger logger) {
		return Cheeta.getInstance(SessionService.class).call(() -> {
			var build = Cheeta.getInstance(BuildService.class).load(buildId);
			CoverageReport result = write(getReportLockName(build), () -> {
				File reportDir = new File(build.getDir(), CoverageStats.CATEGORY + "/" + getReportName());

				FileUtils.createDir(reportDir);
				try {
					CoverageReport aResult = process(build, inputDir, logger);
					if (aResult != null) {
						aResult.getStats().writeTo(reportDir);
						for (var entry: aResult.getStatuses().entrySet())
							writeLineStatuses(build, entry.getKey(), entry.getValue());

						Cheeta.getInstance(ProjectService.class).directoryModified(
								build.getProject().getId(), reportDir.getParentFile());
						return aResult;
					} else {
						FileUtils.deleteDir(reportDir);
						return null;
					}
				} catch (Exception e) {
					FileUtils.deleteDir(reportDir);
					throw ExceptionUtils.unchecked(e);
				}
			});

			if (result != null) {
				var metric = Cheeta.getInstance(BuildMetricService.class).find(CoverageMetric.class, build, getReportName());
				if (metric == null) {
					metric = new CoverageMetric();
					metric.setBuild(build);
					metric.setReportName(getReportName());
				}

				Coverage coverages = result.getStats().getOverallCoverage();
				metric.setBranchCoverage(coverages.getBranchPercentage());
				metric.setLineCoverage(coverages.getLinePercentage());

				Cheeta.getInstance(Dao.class).persist(metric);
			}
			return new ServerStepResult(true);
		});
	}

	@Nullable
	protected abstract CoverageReport process(Build build, File inputDir, TaskLogger logger);

	private void writeLineStatuses(Build build, String blobPath, Map<Integer, CoverageStatus> lineStatuses) {
		if (!lineStatuses.isEmpty()) {
			File reportDir = new File(build.getDir(), CoverageStats.CATEGORY + "/" + getReportName());
			File lineCoverageFile = new File(reportDir, CoverageStats.FILES + "/" + blobPath);
			FileUtils.createDir(lineCoverageFile.getParentFile());
			try (var os = new BufferedOutputStream(new FileOutputStream(lineCoverageFile))) {
				SerializationUtils.serialize((Serializable) lineStatuses, os);
			} catch (IOException e) {
				throw new RuntimeException(e);
			};
		}
	}
	
}

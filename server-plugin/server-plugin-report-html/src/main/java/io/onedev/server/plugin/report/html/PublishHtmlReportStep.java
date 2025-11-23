package io.cheeta.server.plugin.report.html;

import static io.cheeta.commons.utils.LockUtils.write;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import io.cheeta.commons.codeassist.InputSuggestion;
import io.cheeta.commons.utils.FileUtils;
import io.cheeta.commons.utils.TaskLogger;
import io.cheeta.k8shelper.ServerStepResult;
import io.cheeta.server.Cheeta;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.Interpolative;
import io.cheeta.server.buildspec.BuildSpec;
import io.cheeta.server.buildspec.step.PublishReportStep;
import io.cheeta.server.buildspec.step.StepGroup;
import io.cheeta.server.service.BuildService;
import io.cheeta.server.service.ProjectService;
import io.cheeta.server.job.JobContext;
import io.cheeta.server.job.JobService;
import io.cheeta.server.model.Build;
import io.cheeta.server.persistence.SessionService;

@Editable(order=1070, group= StepGroup.PUBLISH, name="Html Report")
public class PublishHtmlReportStep extends PublishReportStep {

	private static final long serialVersionUID = 1L;
	
	public static final String CATEGORY = "html";
	
	public static final String START_PAGE = "$cheeta-htmlreport-startpage$";

	private String startPage;

	@Editable(order=1000, description="Specify start page of the report relative to <a href='https://docs.cheeta.io/concepts#job-workspace'>job workspace</a>, for instance: api/index.html")
	@Interpolative(variableSuggester="suggestVariables")
	@NotEmpty
	public String getStartPage() {
		return startPage;
	}

	public void setStartPage(String startPage) {
		this.startPage = startPage;
	}

	@SuppressWarnings("unused")
	private static List<InputSuggestion> suggestVariables(String matchWith) {
		return BuildSpec.suggestVariables(matchWith, true, true, false);
	}

	@Override
	public ServerStepResult run(Long buildId, File inputDir, TaskLogger logger) {
		return Cheeta.getInstance(SessionService.class).call(() -> {
			var build = Cheeta.getInstance(BuildService.class).load(buildId);
			JobContext jobContext = Cheeta.getInstance(JobService.class).getJobContext(build.getId());
			if (jobContext.getJobExecutor().isHtmlReportPublishEnabled()) {
				write(getReportLockName(build), () -> {
					File reportDir = new File(build.getDir(), CATEGORY + "/" + getReportName());
					File startPage = new File(inputDir, getStartPage());
					if (startPage.exists()) {
						FileUtils.createDir(reportDir);
						File startPageFile = new File(reportDir, START_PAGE);
						FileUtils.writeFile(startPageFile, getStartPage());

						int baseLen = inputDir.getAbsolutePath().length() + 1;
						for (File file : getPatternSet().listFiles(inputDir)) {
							try {
								FileUtils.copyFile(file, new File(reportDir, file.getAbsolutePath().substring(baseLen)));
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						}
						Cheeta.getInstance(ProjectService.class).directoryModified(
								build.getProject().getId(), reportDir.getParentFile());
					} else {
						logger.warning("Html report start page not found: " + startPage.getAbsolutePath());
					}
				});
			} else {
				logger.error("Html report publish is prohibited by current job executor");
				return new ServerStepResult(false);
			}
			return new ServerStepResult(true);
		});		
	}

	public static String getReportLockName(Build build) {
		return getReportLockName(build.getProject().getId(), build.getNumber());
	}

	public static String getReportLockName(Long projectId, Long buildNumber) {
		return PublishHtmlReportStep.class.getName() + ":"	+ projectId + ":" + buildNumber;
	}
	
}

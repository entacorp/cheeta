package io.cheeta.server.plugin.report.markdown;

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
import io.cheeta.server.model.Build;
import io.cheeta.server.persistence.SessionService;

import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static io.cheeta.commons.utils.LockUtils.write;

@Editable(order=1100, group=StepGroup.PUBLISH, name="Markdown Report")
public class PublishMarkdownReportStep extends PublishReportStep {

	private static final long serialVersionUID = 1L;
	
	public static final String CATEGORY = "markdown";
	
	public static final String START_PAGE = "$cheeta-startpage$";
	
	private String startPage;
	
	@Editable(order=1100, description="Specify start page of the report relative to <a href='https://docs.cheeta.io/concepts#job-workspace'>job workspace</a>, for instance: <tt>manual/index.md</tt>")
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

	public static String getReportLockName(Build build) {
		return getReportLockName(build.getProject().getId(), build.getNumber());
	}
	
	public static String getReportLockName(Long projectId, Long buildNumber) {
		return PublishMarkdownReportStep.class.getName() + ":" + projectId + ":" + buildNumber;
	}
	
	@Override
	public ServerStepResult run(Long buildId, File inputDir, TaskLogger logger) {
		Cheeta.getInstance(SessionService.class).run(() -> {
			var build = Cheeta.getInstance(BuildService.class).load(buildId);
			write(getReportLockName(build), () -> {
				File startPage = new File(inputDir, getStartPage());
				if (startPage.exists()) {
					File reportDir = new File(build.getDir(), CATEGORY + "/" + getReportName());

					FileUtils.createDir(reportDir);
					File startPageFile = new File(reportDir, START_PAGE);
					FileUtils.writeFile(startPageFile, getStartPage());

					int baseLen = inputDir.getAbsolutePath().length() + 1;
					for (File file: getPatternSet().listFiles(inputDir)) {
						try {
							FileUtils.copyFile(file, new File(reportDir, file.getAbsolutePath().substring(baseLen)));
						} catch (IOException e) {
							throw new RuntimeException(e);
						}
					}
					Cheeta.getInstance(ProjectService.class).directoryModified(
							build.getProject().getId(), reportDir.getParentFile());
				} else {
					logger.warning("Markdown report start page not found: " + startPage.getAbsolutePath());
				}
				return null;
			});
		});
		return new ServerStepResult(true);
	}

}

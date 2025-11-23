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
import io.cheeta.server.persistence.SessionService;

import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.util.List;

import static io.cheeta.commons.utils.LockUtils.write;
import static java.nio.charset.StandardCharsets.UTF_8;

@Editable(order=1200, group=StepGroup.PUBLISH, name="Pull Request Markdown Report", 
		description="This report will be displayed in pull request overview page if build is triggered by pull request")
public class PublishPullRequestMarkdownReportStep extends PublishReportStep {

	private static final long serialVersionUID = 1L;
	
	public static final String CATEGORY = "pull-request-markdown";
	
	public static final String CONTENT = "content.md";

	private String file;
	
	@Editable(order=1100, description="Specify markdown file relative to <a href='https://docs.cheeta.io/concepts#job-workspace'>job workspace</a> to be published")
	@Interpolative(variableSuggester="suggestVariables")
	@NotEmpty
	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	@Override
	public String getFilePatterns() {
		return getFile();
	}

	@SuppressWarnings("unused")
	private static List<InputSuggestion> suggestVariables(String matchWith) {
		return BuildSpec.suggestVariables(matchWith, true, true, false);
	}

	@Override
	public ServerStepResult run(Long buildId, File workspace, TaskLogger logger) {
		Cheeta.getInstance(SessionService.class).run(() -> {
			var build = Cheeta.getInstance(BuildService.class).load(buildId);
			if (build.getRequest() != null) {
				write(getReportLockName(build.getProject().getId(), build.getNumber()), () -> {
					File file = new File(workspace, getFile());
					if (file.exists()) {
						File reportDir = new File(build.getDir(), CATEGORY + "/" + getReportName());
						String markdown = FileUtils.readFileToString(file, UTF_8);
						FileUtils.createDir(reportDir);
						FileUtils.writeFile(new File(reportDir, CONTENT), markdown, UTF_8);
						Cheeta.getInstance(ProjectService.class).directoryModified(
								build.getProject().getId(), reportDir.getParentFile());
					} else {
						logger.warning("WARNING: Markdown report file not found: " + file.getAbsolutePath());
					}
					return null;
				});
			}
		});			
		return new ServerStepResult(true);
	}

	public static String getReportLockName(Long projectId, Long buildNumber) {
		return PublishPullRequestMarkdownReportStep.class.getName() + ":" + projectId + ":" + buildNumber;
	}

}

package io.cheeta.server.buildspec.step;

import static io.cheeta.server.buildspec.step.StepGroup.PUBLISH;

import java.io.File;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import io.cheeta.commons.codeassist.InputSuggestion;
import io.cheeta.commons.utils.FileUtils;
import io.cheeta.commons.utils.LockUtils;
import io.cheeta.commons.utils.StringUtils;
import io.cheeta.commons.utils.TaskLogger;
import io.cheeta.k8shelper.ServerStepResult;
import io.cheeta.server.Cheeta;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.Interpolative;
import io.cheeta.server.annotation.Patterns;
import io.cheeta.server.annotation.ProjectChoice;
import io.cheeta.server.annotation.SubPath;
import io.cheeta.server.buildspec.BuildSpec;
import io.cheeta.server.service.BuildService;
import io.cheeta.server.service.ProjectService;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.job.JobContext;
import io.cheeta.server.job.JobService;
import io.cheeta.server.model.Project;
import io.cheeta.server.persistence.SessionService;
import io.cheeta.server.util.patternset.PatternSet;

@Editable(order=1060, name="Site", group = PUBLISH, description="This step publishes specified files to be served as project web site. "
		+ "Project web site can be accessed publicly via <code>http://&lt;cheeta base url&gt;/path/to/project/~site</code>")
public class PublishSiteStep extends ServerSideStep {

	private static final long serialVersionUID = 1L;

	private String projectPath;
	
	private String sourcePath;
	
	private String siteFiles;
	
	@Editable(order=10, name="Project", placeholder="Current project", description="Optionally specify the project to "
			+ "publish site files to. Leave empty to publish to current project")
	@ProjectChoice
	public String getProjectPath() {
		return projectPath;
	}
	
	public void setProjectPath(String projectPath) {
		this.projectPath = projectPath;
	}
	
	@Editable(order=50, name="From Directory", placeholder="Job workspace", description="Optionally specify path "
			+ "relative to <a href='https://docs.cheeta.io/concepts#job-workspace'>job workspace</a> to publish "
			+ "artifacts from. Leave empty to use job workspace itself")
	@Interpolative(variableSuggester="suggestVariables")
	@SubPath
	@Override
	public String getSourcePath() {
		return sourcePath;
	}
	
	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}
	
	@Editable(order=100, description="Specify files under above directory to be published. "
			+ "Use * or ? for pattern match. <b>NOTE:</b> <code>index.html</code> should be "
			+ "included in these files to be served as site start page")
	@Interpolative(variableSuggester="suggestVariables")
	@Patterns(path=true)
	@NotEmpty
	public String getArtifacts() {
		return siteFiles;
	}

	public void setArtifacts(String artifacts) {
		this.siteFiles = artifacts;
	}
	
	@SuppressWarnings("unused")
	private static List<InputSuggestion> suggestVariables(String matchWith) {
		return BuildSpec.suggestVariables(matchWith, true, true, false);
	}

	@Override
	protected PatternSet getFiles() {
		return PatternSet.parse(getArtifacts());
	}

	@Override
	public ServerStepResult run(Long buildId, File inputDir, TaskLogger logger) {
		return Cheeta.getInstance(SessionService.class).call(() -> {
			var build = Cheeta.getInstance(BuildService.class).load(buildId);
			JobContext jobContext = Cheeta.getInstance(JobService.class).getJobContext(build.getId());
			if (jobContext.getJobExecutor().isSitePublishEnabled()) {
				Project project;
				if (projectPath != null) {
					project = Cheeta.getInstance(ProjectService.class).findByPath(projectPath);
					if (project == null) {
						logger.error("Unable to find project: " + projectPath);
						return new ServerStepResult(false);
					}
				} else {
					project = build.getProject();
				}
				var projectId = project.getId();
				LockUtils.write(project.getSiteLockName(), () -> {
					File projectSiteDir = Cheeta.getInstance(ProjectService.class).getSiteDir(projectId);
					FileUtils.cleanDir(projectSiteDir);
					FileUtils.copyDirectory(inputDir, projectSiteDir);
					Cheeta.getInstance(ProjectService.class).directoryModified(projectId, projectSiteDir);
					return null;
				});
				String serverUrl = Cheeta.getInstance(SettingService.class).getSystemSetting().getServerUrl();
				logger.log("Site published as "
						+ StringUtils.stripEnd(serverUrl, "/") + "/" + project.getPath() + "/~site");
			} else {
				logger.error("Site publish is prohibited by current job executor");
				return new ServerStepResult(false);
			}
			return new ServerStepResult(true);
		});
	}
	
}

package io.cheeta.server.buildspec.step;

import io.cheeta.commons.codeassist.InputSuggestion;
import io.cheeta.commons.utils.FileUtils;
import io.cheeta.commons.utils.LockUtils;
import io.cheeta.commons.utils.TaskLogger;
import io.cheeta.k8shelper.ServerStepResult;
import io.cheeta.server.Cheeta;
import io.cheeta.server.StorageService;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.Interpolative;
import io.cheeta.server.annotation.Patterns;
import io.cheeta.server.annotation.SubPath;
import io.cheeta.server.buildspec.BuildSpec;
import io.cheeta.server.service.BuildService;
import io.cheeta.server.service.ProjectService;
import io.cheeta.server.persistence.SessionService;
import io.cheeta.server.util.patternset.PatternSet;

import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.util.List;

import static io.cheeta.server.buildspec.step.StepGroup.PUBLISH;

@Editable(order=1050, group= PUBLISH, name="Artifacts", description="This step copies files from job workspace " + 
		"to build artifacts directory, so that they can be accessed after job is completed")
public class PublishArtifactStep extends ServerSideStep {

	private static final long serialVersionUID = 1L;

	private String sourcePath;
	
	private String artifacts;
	
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
	
	@Editable(order=100, description="Specify files under above directory to be published")
	@Interpolative(variableSuggester="suggestVariables")
	@Patterns(path=true)
	@NotEmpty
	public String getArtifacts() {
		return artifacts;
	}

	public void setArtifacts(String artifacts) {
		this.artifacts = artifacts;
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
	public ServerStepResult run(Long buildId, File inputDir, TaskLogger jobLogger) {
		return Cheeta.getInstance(SessionService.class).call(() -> {
			var build = Cheeta.getInstance(BuildService.class).load(buildId);
			return LockUtils.write(build.getArtifactsLockName(), () -> {
				var projectId = build.getProject().getId();
				var artifactsDir = Cheeta.getInstance(StorageService.class).initArtifactsDir(projectId, build.getNumber());
				FileUtils.copyDirectory(inputDir, artifactsDir);
				Cheeta.getInstance(ProjectService.class).directoryModified(projectId, artifactsDir);
				return new ServerStepResult(true);
			});
		});
	}

}

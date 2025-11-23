package io.cheeta.server.buildspec.step;

import io.cheeta.commons.codeassist.InputSuggestion;
import io.cheeta.commons.utils.TaskLogger;
import io.cheeta.k8shelper.ServerStepResult;
import io.cheeta.server.Cheeta;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.Interpolative;
import io.cheeta.server.buildspec.BuildSpec;
import io.cheeta.server.service.BuildService;
import io.cheeta.server.event.ListenerRegistry;
import io.cheeta.server.event.project.build.BuildUpdated;
import io.cheeta.server.persistence.TransactionService;

import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.cheeta.k8shelper.KubernetesHelper.BUILD_VERSION;

@Editable(order=260, name="Set Build Version")
public class SetBuildVersionStep extends ServerSideStep {

	private static final long serialVersionUID = 1L;

	private String buildVersion;

	@Editable(order=100)
	@Interpolative(variableSuggester="suggestVariables")
	@NotEmpty
	public String getBuildVersion() {
		return buildVersion;
	}

	public void setBuildVersion(String buildVersion) {
		this.buildVersion = buildVersion;
	}

	@SuppressWarnings("unused")
	private static List<InputSuggestion> suggestVariables(String matchWith) {
		return BuildSpec.suggestVariables(matchWith, false, true, false);
	}
	
	@Override
	public ServerStepResult run(Long buildId, File inputDir, TaskLogger logger) {
		return Cheeta.getInstance(TransactionService.class).call(() -> {
			var build = Cheeta.getInstance(BuildService.class).load(buildId);
			build.setVersion(buildVersion);
			Cheeta.getInstance(ListenerRegistry.class).post(new BuildUpdated(build));
			Map<String, byte[]> outputFiles = new HashMap<>();
			outputFiles.put(BUILD_VERSION, buildVersion.getBytes(StandardCharsets.UTF_8));
			return new ServerStepResult(true, outputFiles);
		});
		
	}

}

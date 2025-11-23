package io.cheeta.server.buildspec.step;

import io.cheeta.commons.codeassist.InputSuggestion;
import io.cheeta.commons.utils.TaskLogger;
import io.cheeta.k8shelper.ServerStepResult;
import io.cheeta.server.Cheeta;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.Interpolative;
import io.cheeta.server.annotation.Markdown;
import io.cheeta.server.buildspec.BuildSpec;
import io.cheeta.server.service.BuildService;
import io.cheeta.server.event.ListenerRegistry;
import io.cheeta.server.event.project.build.BuildUpdated;
import io.cheeta.server.persistence.TransactionService;

import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.util.List;

@Editable(order=265, name="Set Build Description")
public class SetBuildDescriptionStep extends ServerSideStep {

	private static final long serialVersionUID = 1L;

	private String buildDescription;

	@Editable(order=100)
	@Interpolative(variableSuggester="suggestVariables")
	@NotEmpty
	@Markdown
	public String getBuildDescription() {
		return buildDescription;
	}

	public void setBuildDescription(String buildDescription) {
		this.buildDescription = buildDescription;
	}

	@SuppressWarnings("unused")
	private static List<InputSuggestion> suggestVariables(String matchWith) {
		return BuildSpec.suggestVariables(matchWith, false, true, false);
	}
	
	@Override
	public ServerStepResult run(Long buildId, File inputDir, TaskLogger jobLogger) {
		return Cheeta.getInstance(TransactionService.class).call(() -> {
			var build = Cheeta.getInstance(BuildService.class).load(buildId);
			build.setDescription(buildDescription);
			Cheeta.getInstance(ListenerRegistry.class).post(new BuildUpdated(build));
			return new ServerStepResult(true);
		});
		
	}

}

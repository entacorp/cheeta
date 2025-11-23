package io.cheeta.server.buildspec.job.projectdependency;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import io.cheeta.commons.codeassist.InputSuggestion;
import io.cheeta.server.Cheeta;
import io.cheeta.server.buildspec.BuildSpec;
import io.cheeta.server.service.BuildService;
import io.cheeta.server.model.Build;
import io.cheeta.server.model.Project;
import io.cheeta.server.util.EditContext;
import io.cheeta.server.web.behavior.inputassist.InputAssistBehavior;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.Interpolative;
import io.cheeta.server.annotation.OmitName;
import io.cheeta.server.web.util.SuggestionUtils;

@Editable(order=200, name="Specify by Build Number")
public class SpecifiedBuild implements BuildProvider {

	private static final long serialVersionUID = 1L;
	
	private String buildNumber;
	
	@Editable(order=300)
	@OmitName
	@Interpolative(variableSuggester="suggestVariables", literalSuggester="suggestBuilds")
	@NotEmpty
	public String getBuildNumber() {
		return buildNumber;
	}

	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}

	@SuppressWarnings("unused")
	private static List<InputSuggestion> suggestVariables(String matchWith) {
		return BuildSpec.suggestVariables(matchWith, false, false, false);
	}
	
	@SuppressWarnings("unused")
	private static List<InputSuggestion> suggestBuilds(String matchWith) {
		Project project = ProjectDependency.getInputProject(EditContext.get(1));
		if (project != null) 
			return SuggestionUtils.suggestBuilds(project, matchWith, InputAssistBehavior.MAX_SUGGESTIONS);
		else
			return new ArrayList<>();
	}

	@Override
	public Build getBuild(Project project) {
		Long buildNumber;
		if (this.buildNumber.startsWith("#"))
			buildNumber = Long.parseLong(this.buildNumber.substring(1));
		else
			buildNumber = Long.parseLong(this.buildNumber);
		
		return Cheeta.getInstance(BuildService.class).find(project, buildNumber);
	}

	@Override
	public String getDescription() {
		return buildNumber;
	}

}

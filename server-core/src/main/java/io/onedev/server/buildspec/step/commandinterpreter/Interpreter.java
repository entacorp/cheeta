package io.cheeta.server.buildspec.step.commandinterpreter;

import io.cheeta.commons.codeassist.InputSuggestion;
import io.cheeta.k8shelper.CommandFacade;
import io.cheeta.k8shelper.RegistryLoginFacade;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.buildspec.BuildSpec;
import io.cheeta.server.model.support.administration.jobexecutor.JobExecutor;

import org.jspecify.annotations.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Editable
public abstract class Interpreter implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String commands;
	
	public String getCommands() {
		return commands;
	}

	public void setCommands(String commands) {
		this.commands = commands;
	}

	public abstract CommandFacade getExecutable(JobExecutor jobExecutor, String jobToken, @Nullable String image,
												@Nullable String runAs, List<RegistryLoginFacade> registryLogins,
												Map<String, String> envMap, boolean useTTY);
	
	static List<InputSuggestion> suggestVariables(String matchWith) {
		return BuildSpec.suggestVariables(matchWith, true, false, true);
	}
	
}

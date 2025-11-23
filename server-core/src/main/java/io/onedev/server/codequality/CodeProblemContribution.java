package io.cheeta.server.codequality;

import java.util.List;

import org.jspecify.annotations.Nullable;

import io.cheeta.commons.loader.ExtensionPoint;
import io.cheeta.server.model.Build;

@ExtensionPoint
public interface CodeProblemContribution {

	List<CodeProblem> getCodeProblems(Build build, String blobPath, @Nullable String reportName); 
	
}

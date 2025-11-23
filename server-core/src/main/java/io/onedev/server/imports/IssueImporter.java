package io.cheeta.server.imports;

import java.io.Serializable;
import java.util.List;

import io.cheeta.commons.utils.TaskLogger;
import io.cheeta.server.web.component.taskbutton.TaskResult;
import io.cheeta.server.web.util.ImportStep;

public interface IssueImporter extends Serializable {

	String getName();

	List<ImportStep<? extends Serializable>> getSteps();
	
	public abstract TaskResult doImport(Long projectId, boolean dryRun, TaskLogger logger);
	
}

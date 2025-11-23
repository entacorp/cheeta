package io.cheeta.server.imports;

import io.cheeta.commons.utils.TaskLogger;
import io.cheeta.server.web.component.taskbutton.TaskResult;
import io.cheeta.server.web.util.ImportStep;

import java.io.Serializable;
import java.util.List;

public interface ProjectImporter extends Serializable {

	String getName();
	
	List<ImportStep<? extends Serializable>> getSteps();

	TaskResult doImport(boolean dryRun, TaskLogger logger);
	
}

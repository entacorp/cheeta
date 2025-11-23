package io.cheeta.server.job;

import io.cheeta.commons.utils.TaskLogger;
import io.cheeta.server.terminal.Shell;
import io.cheeta.server.terminal.Terminal;

import java.io.Serializable;

public interface JobRunnable extends Serializable {
	
	boolean run(TaskLogger jobLogger);

	void resume(JobContext jobContext);

	Shell openShell(JobContext jobContext, Terminal terminal);
	
}

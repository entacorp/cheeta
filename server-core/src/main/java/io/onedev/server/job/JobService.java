package io.cheeta.server.job;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.jspecify.annotations.Nullable;

import org.eclipse.jgit.lib.ObjectId;

import io.cheeta.commons.utils.TaskLogger;
import io.cheeta.k8shelper.ServerStepResult;
import io.cheeta.server.cluster.ClusterTask;
import io.cheeta.server.model.Build;
import io.cheeta.server.model.Issue;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.User;
import io.cheeta.server.terminal.Shell;
import io.cheeta.server.terminal.Terminal;
import io.cheeta.server.terminal.WebShell;

public interface JobService {
	
	Build submit(User user, Project project, ObjectId commitId, String jobName,
				 Map<String, List<String>> paramMap, String refName, 
				 @Nullable PullRequest request, @Nullable Issue issue, String reason);
	
	void resubmit(User user, Build build, String reason);
	
	void cancel(Build build);
	
	void resume(Build build);

	boolean runJob(String server, ClusterTask<Boolean> runnable);

	boolean runJob(JobContext jobContext, JobRunnable runnable);
	
	WebShell openShell(Build build, Terminal terminal);
	
	JobContext getJobContext(String jobToken, boolean mustExist);
	
	@Nullable
	Shell getShell(String sessionId);
	
	void reportJobWorkspace(JobContext jobContext, String workspacePath);
	
	@Nullable
	JobContext getJobContext(Long buildId);

	void copyDependencies(JobContext jobContext, File targetDir);
	
	ServerStepResult runServerStep(JobContext jobContext, List<Integer> stepPosition, File inputDir,
								   Map<String, String> placeholderValues, boolean callByAgent,
								   TaskLogger logger);
	
}

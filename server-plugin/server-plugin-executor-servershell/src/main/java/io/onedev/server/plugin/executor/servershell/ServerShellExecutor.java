package io.cheeta.server.plugin.executor.servershell;

import static io.cheeta.agent.ExecutorUtils.newInfoLogger;
import static io.cheeta.agent.ExecutorUtils.newWarningLogger;
import static io.cheeta.agent.ShellExecutorUtils.testCommands;
import static io.cheeta.k8shelper.KubernetesHelper.cloneRepository;
import static io.cheeta.k8shelper.KubernetesHelper.installGitCert;
import static io.cheeta.k8shelper.KubernetesHelper.replacePlaceholders;
import static io.cheeta.k8shelper.KubernetesHelper.stringifyStepPosition;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotEmpty;

import org.apache.commons.lang.SystemUtils;

import io.cheeta.agent.ExecutorUtils;
import io.cheeta.commons.bootstrap.Bootstrap;
import io.cheeta.commons.bootstrap.SecretMasker;
import io.cheeta.commons.loader.AppLoader;
import io.cheeta.commons.utils.ExplicitException;
import io.cheeta.commons.utils.FileUtils;
import io.cheeta.commons.utils.TaskLogger;
import io.cheeta.commons.utils.command.Commandline;
import io.cheeta.k8shelper.BuildImageFacade;
import io.cheeta.k8shelper.CheckoutFacade;
import io.cheeta.k8shelper.CloneInfo;
import io.cheeta.k8shelper.CommandFacade;
import io.cheeta.k8shelper.CompositeFacade;
import io.cheeta.k8shelper.LeafFacade;
import io.cheeta.k8shelper.LeafHandler;
import io.cheeta.k8shelper.PruneBuilderCacheFacade;
import io.cheeta.k8shelper.RunContainerFacade;
import io.cheeta.k8shelper.RunImagetoolsFacade;
import io.cheeta.k8shelper.ServerSideFacade;
import io.cheeta.k8shelper.ServerStepResult;
import io.cheeta.k8shelper.SetupCacheFacade;
import io.cheeta.server.Cheeta;
import io.cheeta.server.annotation.Code;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.Numeric;
import io.cheeta.server.annotation.OmitName;
import io.cheeta.server.cluster.ClusterService;
import io.cheeta.server.cluster.ClusterTask;
import io.cheeta.server.git.location.GitLocation;
import io.cheeta.server.job.JobContext;
import io.cheeta.server.job.JobService;
import io.cheeta.server.job.JobRunnable;
import io.cheeta.server.job.ResourceAllocator;
import io.cheeta.server.job.ServerCacheHelper;
import io.cheeta.server.model.support.administration.jobexecutor.JobExecutor;
import io.cheeta.server.plugin.executor.servershell.ServerShellExecutor.TestData;
import io.cheeta.server.terminal.CommandlineShell;
import io.cheeta.server.terminal.Shell;
import io.cheeta.server.terminal.Terminal;
import io.cheeta.server.web.util.Testable;

@Editable(order=ServerShellExecutor.ORDER, name="Server Shell Executor", description="" +
		"This executor runs build jobs with Cheeta server's shell facility.<br>" +
		"<b class='text-danger'>WARNING</b>: Jobs running with this executor has same " +
		"permission as Cheeta server process. Make sure it can only be used by trusted jobs")
public class ServerShellExecutor extends JobExecutor implements Testable<TestData> {

	private static final long serialVersionUID = 1L;
	
	static final int ORDER = 400;
	
	private String concurrency;
	
	private transient volatile LeafFacade runningStep;
	
	private transient volatile File buildHome;

	@Editable(order=1000, description = "" +
			"Specify max number of jobs this executor can run concurrently. " +
			"Leave empty to set as CPU cores")
	@Numeric
	public String getConcurrency() {
		return concurrency;
	}

	public void setConcurrency(String concurrency) {
		this.concurrency = concurrency;
	}

	private ClusterService getClusterService() {
		return Cheeta.getInstance(ClusterService.class);
	}
	
	private JobService getJobService() {
		return Cheeta.getInstance(JobService.class);
	}
	
	private ResourceAllocator getResourceAllocator() {
		return Cheeta.getInstance(ResourceAllocator.class);
	}

	private int getConcurrencyNumber() {
		if (getConcurrency() != null)
			return Integer.parseInt(getConcurrency());
		else
			return 0;
	}
	
	@Override
	public boolean execute(JobContext jobContext, TaskLogger jobLogger) {
		ClusterTask<Boolean> runnable = () -> getJobService().runJob(jobContext, new JobRunnable() {

			@Override
			public boolean run(TaskLogger jobLogger) {
				notifyJobRunning(jobContext.getBuildId(), null);
				checkApplicable();
				
				buildHome = new File(Bootstrap.getTempDir(),
						"cheeta-build-" + jobContext.getProjectId() + "-" + jobContext.getBuildNumber() + "-" + jobContext.getSubmitSequence());
				FileUtils.createDir(buildHome);
				File workspaceDir = new File(buildHome, "workspace");
				SecretMasker.push(jobContext.getSecretMasker());
				try {
					String localServer = getClusterService().getLocalServerAddress();
					jobLogger.log(String.format("Executing job (executor: %s, server: %s)...", 
							getName(), localServer));

					jobLogger.log(String.format("Executing job with executor '%s'...", getName()));

					if (!jobContext.getServices().isEmpty()) {
						throw new ExplicitException("This job requires services, which can only be supported "
								+ "by docker aware executors");
					}
					FileUtils.createDir(workspaceDir);

					var cacheHelper = new ServerCacheHelper(buildHome, jobContext, jobLogger);
					
					jobLogger.log("Copying job dependencies...");
					getJobService().copyDependencies(jobContext, workspaceDir);

					File userHome = new File(buildHome, "user");
					FileUtils.createDir(userHome);

					getJobService().reportJobWorkspace(jobContext, workspaceDir.getAbsolutePath());
					CompositeFacade entryFacade = new CompositeFacade(jobContext.getActions());

					var successful = entryFacade.execute(new LeafHandler() {

						@Override
						public boolean execute(LeafFacade facade, List<Integer> position) {
							return ExecutorUtils.runStep(entryFacade, position, jobLogger, () -> {
								runningStep = facade;
								try {
									return doExecute(facade, position);
								} finally {
									runningStep = null;
								}
							});
						}
						
						private boolean doExecute(LeafFacade facade, List<Integer> position) {
							if (facade instanceof CommandFacade) {
								CommandFacade commandFacade = (CommandFacade) facade;
								if (commandFacade.getImage() != null) {
									throw new ExplicitException("This step can only be executed by server docker executor, "
											+ "remote docker executor, or kubernetes executor");
								}

								commandFacade.generatePauseCommand(buildHome);

								var commandDir = new File(buildHome, "command");
								FileUtils.createDir(commandDir);
								File stepScriptFile = new File(commandDir, "step-" + stringifyStepPosition(position) + commandFacade.getScriptExtension());
								try {
									FileUtils.writeStringToFile(
											stepScriptFile,
											commandFacade.normalizeCommands(replacePlaceholders(commandFacade.getCommands(), buildHome)),
											StandardCharsets.UTF_8);
								} catch (IOException e) {
									throw new RuntimeException(e);
								}

								Commandline interpreter = commandFacade.getScriptInterpreter();
								Map<String, String> environments = new HashMap<>();
								environments.put("GIT_HOME", userHome.getAbsolutePath());
								environments.put("ONEDEV_WORKSPACE", workspaceDir.getAbsolutePath());
								environments.putAll(commandFacade.getEnvMap());
								interpreter.workingDir(workspaceDir).environments(environments);
								interpreter.addArgs(stepScriptFile.getAbsolutePath());

								var result = interpreter.execute(newInfoLogger(jobLogger), newWarningLogger(jobLogger));
								if (result.getReturnCode() != 0) {
									jobLogger.error("Command exited with code " + result.getReturnCode());
									return false;
								}
							} else if (facade instanceof BuildImageFacade || facade instanceof RunContainerFacade
									|| facade instanceof RunImagetoolsFacade || facade instanceof PruneBuilderCacheFacade) {
								throw new ExplicitException("This step can only be executed by server docker executor or "
										+ "remote docker executor");
							} else if (facade instanceof CheckoutFacade) {
								CheckoutFacade checkoutFacade = (CheckoutFacade) facade;
								jobLogger.log("Checking out code...");

								Commandline git = new Commandline(AppLoader.getInstance(GitLocation.class).getExecutable());

								Map<String, String> environments = new HashMap<>();
								environments.put("HOME", userHome.getAbsolutePath());
								git.environments(environments);

								checkoutFacade.setupWorkingDir(git, workspaceDir);

								File trustCertsFile = new File(buildHome, "trust-certs.pem");
								installGitCert(git, Bootstrap.getTrustCertsDir(), trustCertsFile,
										trustCertsFile.getAbsolutePath(),
										newInfoLogger(jobLogger),
										newWarningLogger(jobLogger));

								CloneInfo cloneInfo = checkoutFacade.getCloneInfo();
								cloneInfo.writeAuthData(userHome, git, false,
										newInfoLogger(jobLogger),
										newWarningLogger(jobLogger));

								int cloneDepth = checkoutFacade.getCloneDepth();

								cloneRepository(git, jobContext.getProjectGitDir(), cloneInfo.getCloneUrl(), jobContext.getRefName(),
										jobContext.getCommitId().name(), checkoutFacade.isWithLfs(), checkoutFacade.isWithSubmodules(),
										cloneDepth, newInfoLogger(jobLogger), newWarningLogger(jobLogger));
							} else if (facade instanceof SetupCacheFacade) {
								SetupCacheFacade setupCacheFacade = (SetupCacheFacade) facade;
								for (var cachePath: setupCacheFacade.getPaths()) {
									if (new File(cachePath).isAbsolute())
										throw new ExplicitException("Shell executor does not allow absolute cache path: " + cachePath);
								}
								cacheHelper.setupCache(setupCacheFacade);
							} else if (facade instanceof ServerSideFacade) {
								ServerSideFacade serverSideFacade = (ServerSideFacade) facade;
								return serverSideFacade.execute(buildHome, new ServerSideFacade.Runner() {

									@Override
									public ServerStepResult run(File inputDir, Map<String, String> placeholderValues) {
										return getJobService().runServerStep(jobContext, position, inputDir,
												placeholderValues, false, jobLogger);
									}

								});
							} else {
								throw new ExplicitException("Unexpected step type: " + facade.getClass());
							}
							return true;
						}

						@Override
						public void skip(LeafFacade facade, List<Integer> position) {
							jobLogger.notice("Step \"" + entryFacade.getPathAsString(position) + "\" is skipped");
						}

					}, new ArrayList<>());

					cacheHelper.buildFinished(successful);
					
					return successful;
				} finally {
					SecretMasker.pop();
					// Fix https://code.cheeta.io/cheeta/server/~issues/597
					if (SystemUtils.IS_OS_WINDOWS && workspaceDir.exists())
						FileUtils.deleteDir(workspaceDir);
					synchronized (buildHome) {
						FileUtils.deleteDir(buildHome);
					}
				}
			}

			@Override
			public void resume(JobContext jobContext) {
				if (buildHome != null) synchronized (buildHome) {
					if (buildHome.exists())
						FileUtils.touchFile(new File(buildHome, "continue"));
				}
			}

			@Override
			public Shell openShell(JobContext jobContext, Terminal terminal) {
				if (buildHome != null) {
					Commandline shell;
					if (runningStep instanceof CommandFacade) {
						CommandFacade commandStep = (CommandFacade) runningStep;
						shell = new Commandline(commandStep.getShell(SystemUtils.IS_OS_WINDOWS, null)[0]);
					} else if (SystemUtils.IS_OS_WINDOWS) {
						shell = new Commandline("cmd");
					} else {
						shell = new Commandline("sh");
					}
					shell.workingDir(new File(buildHome, "workspace"));
					return new CommandlineShell(terminal, shell);
				} else {
					throw new ExplicitException("Shell not ready");
				}
			}
			
		});
		jobLogger.log("Pending resource allocation...");
		return getResourceAllocator().runServerJob(getName(), getConcurrencyNumber(), 
				1, runnable);
	}
	
	private void checkApplicable() {
		if (Cheeta.getK8sService() != null) {
			throw new ExplicitException(""
					+ "Cheeta running inside kubernetes cluster does not support server shell executor. "
					+ "Please use kubernetes executor instead");
		} else if (Bootstrap.isInDocker()) {
			throw new ExplicitException("Server shell executor is only supported when Cheeta is installed "
					+ "directly on bare metal/virtual machine");
		}
	}

	@Override
	public void test(TestData testData, TaskLogger jobLogger) {
		checkApplicable();
		
		Commandline git = new Commandline(AppLoader.getInstance(GitLocation.class).getExecutable());
		testCommands(git, testData.getCommands(), jobLogger);
	}
	
	@Editable(name="Specify Shell/Batch Commands to Run")
	public static class TestData implements Serializable {

		private static final long serialVersionUID = 1L;

		private String commands;

		@Editable
		@OmitName
		@Code(language=Code.SHELL)
		@NotEmpty
		public String getCommands() {
			return commands;
		}

		public void setCommands(String commands) {
			this.commands = commands;
		}
		
	}

}
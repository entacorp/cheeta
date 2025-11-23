package io.cheeta.server.buildspec.step;

import static io.cheeta.server.buildspec.step.StepGroup.DOCKER_IMAGE;

import io.cheeta.k8shelper.PruneBuilderCacheFacade;
import io.cheeta.k8shelper.StepFacade;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.ReservedOptions;
import io.cheeta.server.buildspec.param.ParamCombination;
import io.cheeta.server.model.Build;
import io.cheeta.server.model.support.administration.jobexecutor.DockerAware;
import io.cheeta.server.model.support.administration.jobexecutor.JobExecutor;
import io.cheeta.server.model.support.administration.jobexecutor.KubernetesAware;

@Editable(order=260, name="Prune Builder Cache", group = DOCKER_IMAGE, description="" +
		"Prune image cache of docker buildx builder. This step calls docker builder prune command " +
		"to remove cache of buildx builder specified in server docker executor or remote docker executor")
public class PruneBuilderCacheStep extends Step {

	private static final long serialVersionUID = 1L;
	
	private String options;

	@Editable(order=100, description = "Optionally specify options for docker builder prune command")
	@ReservedOptions({"-f", "--force", "--builder"})
	public String getOptions() {
		return options;
	}

	public void setOptions(String options) {
		this.options = options;
	}

	@Override
	public StepFacade getFacade(Build build, JobExecutor jobExecutor, String jobToken, ParamCombination paramCombination) {
		return new PruneBuilderCacheFacade(getOptions());
	}
	
	@Override
	public boolean isApplicable(Build build, JobExecutor executor) {
		return executor instanceof DockerAware && !(executor instanceof KubernetesAware);
	}

}

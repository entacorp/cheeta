package io.cheeta.server.buildspec.step;

import static io.cheeta.k8shelper.ExecuteCondition.SUCCESSFUL;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import io.cheeta.k8shelper.Action;
import io.cheeta.k8shelper.ExecuteCondition;
import io.cheeta.k8shelper.StepFacade;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.buildspec.param.ParamCombination;
import io.cheeta.server.model.Build;
import io.cheeta.server.model.support.administration.jobexecutor.JobExecutor;

@Editable
public abstract class Step implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String name;

	private ExecuteCondition condition = SUCCESSFUL;
		
	private boolean optional;

	@Editable(order=10)
	@NotEmpty
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Editable(order=10000, description="Under which condition this step should run. <b>SUCCESSFUL</b> means all " +
			"non-optional steps running before this step are successful")
	@NotNull
	public ExecuteCondition getCondition() {
		return condition;
	}
	
	public void setCondition(ExecuteCondition condition) {
		this.condition = condition;
	}

	@Editable(order=10100, description="Whether or not this step is optional. Execution failure of an optional " +
			"step will not cause the build to fail, and successful condition of subsequent steps will not " +
			"take optional step into account")
	public boolean isOptional() {
		return optional;
	}

	public void setOptional(boolean optional) {
		this.optional = optional;
	}

	public Action getAction(String name, Build build, JobExecutor jobExecutor, String jobToken, ParamCombination paramCombination) {
		return new Action(name, getFacade(build, jobExecutor, jobToken, paramCombination), condition, isOptional());
	}
	
	public Action getAction(Build build, JobExecutor jobExecutor, String jobToken, ParamCombination paramCombination) {
		return getAction(name, build, jobExecutor, jobToken, paramCombination);
	}
	
	public abstract StepFacade getFacade(Build build, JobExecutor jobExecutor, String jobToken, ParamCombination paramCombination);

    public abstract boolean isApplicable(Build build, JobExecutor executor);

}

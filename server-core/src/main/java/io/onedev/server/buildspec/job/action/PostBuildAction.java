package io.cheeta.server.buildspec.job.action;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import io.cheeta.server.buildspec.BuildSpec;
import io.cheeta.server.buildspec.job.Job;
import io.cheeta.server.model.Build;
import io.cheeta.server.annotation.ActionCondition;
import io.cheeta.server.annotation.Editable;

@Editable
public abstract class PostBuildAction implements Serializable {

	private static final long serialVersionUID = 1L;

	private String condition;

	@Editable(order=100, description="Specify the condition current build must satisfy to execute this action")
	@ActionCondition
	@NotEmpty
	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
	
	public abstract void execute(Build build);
	
	public abstract String getDescription();
	
	public void validateWith(BuildSpec buildSpec, Job job) {
		io.cheeta.server.buildspec.job.action.condition.ActionCondition.parse(job, condition);
	}

}

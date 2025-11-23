package io.cheeta.server.web.page.admin.buildsetting.jobexecutor;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import io.cheeta.server.model.support.administration.jobexecutor.JobExecutor;
import io.cheeta.server.annotation.Editable;

@Editable
public class JobExecutorBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private JobExecutor executor;

	@Editable(name="Type")
	@NotNull
	public JobExecutor getExecutor() {
		return executor;
	}

	public void setExecutor(JobExecutor executor) {
		this.executor = executor;
	}
	
}

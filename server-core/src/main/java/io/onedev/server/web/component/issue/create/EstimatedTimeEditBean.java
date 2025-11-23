package io.cheeta.server.web.component.issue.create;

import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.WorkingPeriod;

import javax.validation.constraints.Min;
import java.io.Serializable;

@Editable
public class EstimatedTimeEditBean implements Serializable {
	
	private Integer estimatedTime;

	@Editable(description="Optionally specify estimated time.")
	@WorkingPeriod
	@Min(1)
	public Integer getEstimatedTime() {
		return estimatedTime;
	}

	public void setEstimatedTime(Integer estimatedTime) {
		this.estimatedTime = estimatedTime;
	}

}

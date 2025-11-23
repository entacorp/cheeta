package io.cheeta.server.buildspec.param.instance;

import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.OmitName;
import io.cheeta.server.annotation.ParamSpecProvider;
import io.cheeta.server.annotation.VariableOption;
import io.cheeta.server.buildspec.job.JobDependency;
import io.cheeta.server.buildspec.job.action.RunJobAction;
import io.cheeta.server.buildspec.job.trigger.JobTrigger;
import io.cheeta.server.buildspec.param.spec.ParamSpec;
import io.cheeta.server.buildspec.step.UseTemplateStep;
import io.cheeta.server.util.ComponentContext;
import io.cheeta.server.web.editable.buildspec.job.jobdependency.JobDependencyEditPanel;
import io.cheeta.server.web.editable.buildspec.job.postbuildaction.PostBuildActionEditPanel;
import io.cheeta.server.web.editable.buildspec.job.trigger.JobTriggerEditPanel;
import io.cheeta.server.web.util.WicketUtils;
import org.apache.wicket.Component;

import javax.validation.Valid;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Editable
public class ParamMap implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<ParamInstance> params = new ArrayList<>();

	@Editable
	@ParamSpecProvider("getParamSpecs")
	@VariableOption(withBuildVersion=false, withDynamicVariables=false)
	@OmitName
	@Valid
	public List<ParamInstance> getParams() {
		return params;
	}

	public void setParams(List<ParamInstance> params) {
		this.params = params;
	}
	
	@SuppressWarnings("unused")
	private static List<ParamSpec> getParamSpecs() {
		Component component = ComponentContext.get().getComponent();
		if (WicketUtils.findInnermost(component, JobTriggerEditPanel.class) != null)
			return JobTrigger.getParamSpecs();
		else if (WicketUtils.findInnermost(component, PostBuildActionEditPanel.class) != null)
			return RunJobAction.getParamSpecs();
		else if (WicketUtils.findInnermost(component, JobDependencyEditPanel.class) != null)
			return JobDependency.getParamSpecs();
		else 
			return UseTemplateStep.getParamSpecs();
	}
	
}

package io.cheeta.server.buildspecmodel.inputspec;

import java.util.List;

import org.jspecify.annotations.Nullable;

import io.cheeta.server.util.ComponentContext;
import io.cheeta.server.web.util.WicketUtils;

public interface InputContext {

	List<String> getInputNames();
	
	@Nullable
	InputSpec getInputSpec(String inputName);

	@Nullable
	public static InputContext get() {
		ComponentContext componentContext = ComponentContext.get();
		if (componentContext != null)
			return WicketUtils.findInnermost(componentContext.getComponent(), InputContext.class);
		else
			return null;
	}
	
}
 
package io.cheeta.server.buildspec;

import java.util.List;

import io.cheeta.server.buildspec.param.spec.ParamSpec;

public interface ParamSpecAware {

	List<ParamSpec> getParamSpecs();
	
}

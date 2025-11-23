package io.cheeta.server.buildspec.param.instance;

import io.cheeta.server.annotation.Editable;
import io.cheeta.server.buildspec.param.ParamCombination;
import io.cheeta.server.model.Build;

import org.jspecify.annotations.Nullable;
import java.io.Serializable;
import java.util.List;

@Editable
public interface ValueProvider extends Serializable {
	
	List<String> getValue(@Nullable Build build, @Nullable ParamCombination paramCombination);
	
}

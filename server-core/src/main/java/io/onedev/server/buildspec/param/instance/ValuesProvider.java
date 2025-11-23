package io.cheeta.server.buildspec.param.instance;

import java.io.Serializable;
import java.util.List;

import org.jspecify.annotations.Nullable;

import io.cheeta.server.buildspec.param.ParamCombination;
import io.cheeta.server.model.Build;
import io.cheeta.server.annotation.Editable;

@Editable
public interface ValuesProvider extends Serializable {
	
	List<List<String>> getValues(@Nullable Build build, @Nullable ParamCombination paramCombination);
	
}

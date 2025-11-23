package io.cheeta.server.codequality;

import java.util.Map;

import org.jspecify.annotations.Nullable;

import io.cheeta.commons.loader.ExtensionPoint;
import io.cheeta.server.model.Build;

@ExtensionPoint
public interface LineCoverageContribution {

	Map<Integer, CoverageStatus> getLineCoverages(Build build, String blobPath, @Nullable String reportName); 
	
}

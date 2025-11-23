package io.cheeta.server.imports;

import java.util.Collection;

import io.cheeta.commons.loader.ExtensionPoint;

@ExtensionPoint
public interface ProjectImporterContribution {

	Collection<ProjectImporter> getImporters();
	
	int getOrder();
	
}

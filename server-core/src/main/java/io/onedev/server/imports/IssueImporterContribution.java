package io.cheeta.server.imports;

import java.util.Collection;

import io.cheeta.commons.loader.ExtensionPoint;

@ExtensionPoint
public interface IssueImporterContribution {

	Collection<IssueImporter> getImporters();
	
	int getOrder();
	
}

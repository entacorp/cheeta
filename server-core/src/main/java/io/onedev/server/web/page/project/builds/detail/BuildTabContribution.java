package io.cheeta.server.web.page.project.builds.detail;

import java.util.List;

import io.cheeta.commons.loader.ExtensionPoint;
import io.cheeta.server.model.Build;

@ExtensionPoint
public interface BuildTabContribution {
	
	List<BuildTab> getTabs(Build build);
	
	int getOrder();
	
}

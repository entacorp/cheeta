package io.cheeta.server.web.page.project;

import java.util.List;

import io.cheeta.server.model.Project;
import io.cheeta.server.web.page.layout.SidebarMenuItem;

public interface ProjectMenuContribution {

	List<SidebarMenuItem> getMenuItems(Project project);
	
	int getOrder();
	
}

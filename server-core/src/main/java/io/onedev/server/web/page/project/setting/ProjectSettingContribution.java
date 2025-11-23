package io.cheeta.server.web.page.project.setting;

import java.util.List;

import io.cheeta.commons.loader.ExtensionPoint;

@ExtensionPoint
public interface ProjectSettingContribution {

	List<Class<? extends ContributedProjectSetting>> getSettingClasses();
	
}

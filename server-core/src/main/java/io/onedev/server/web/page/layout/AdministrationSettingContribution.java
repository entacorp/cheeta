package io.cheeta.server.web.page.layout;

import java.util.List;

import io.cheeta.commons.loader.ExtensionPoint;

@ExtensionPoint
public interface AdministrationSettingContribution {

	List<Class<? extends ContributedAdministrationSetting>> getSettingClasses();
	
}

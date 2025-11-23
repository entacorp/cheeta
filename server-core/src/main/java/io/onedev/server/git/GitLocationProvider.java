package io.cheeta.server.git;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import io.cheeta.server.service.SettingService;
import io.cheeta.server.git.location.GitLocation;

@Singleton
public class GitLocationProvider implements Provider<GitLocation> {

	private final SettingService settingService;
	
	@Inject
	public GitLocationProvider(SettingService settingService) {
		this.settingService = settingService;
	}
	
	@Override
	public GitLocation get() {
		return settingService.getSystemSetting().getGitLocation();
	}

}

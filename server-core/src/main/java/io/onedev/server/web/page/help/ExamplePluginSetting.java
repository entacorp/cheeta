package io.cheeta.server.web.page.help;

import io.cheeta.server.annotation.Editable;
import io.cheeta.server.web.page.layout.ContributedAdministrationSetting;
import io.cheeta.server.web.page.project.setting.ContributedProjectSetting;

@Editable
public class ExamplePluginSetting implements ContributedAdministrationSetting, ContributedProjectSetting {

	private static final long serialVersionUID = 1L;

	private String exampleProperty;

	@Editable
	public String getExampleProperty() {
		return exampleProperty;
	}

	public void setExampleProperty(String exampleProperty) {
		this.exampleProperty = exampleProperty;
	}
	
}
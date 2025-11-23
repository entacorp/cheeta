package io.cheeta.server.plugin.pack.container;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.SettingService;
import org.apache.wicket.markup.html.panel.Panel;

public class InsecureRegistryNotePanel extends Panel {
	
	public InsecureRegistryNotePanel(String id) {
		super(id);
	}

	private String getServerUrl() {
		return Cheeta.getInstance(SettingService.class).getSystemSetting().getServerUrl();
	}
	
	@Override
	protected void onConfigure() {
		super.onConfigure();
		setVisible(getServerUrl().startsWith("http://"));
	}
	
}

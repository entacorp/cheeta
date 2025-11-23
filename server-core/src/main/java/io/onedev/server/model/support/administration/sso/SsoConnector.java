package io.cheeta.server.model.support.administration.sso;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import io.cheeta.server.Cheeta;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.web.page.security.SsoProcessPage;

@Editable
public abstract class SsoConnector implements Serializable {
	
	private static final long serialVersionUID = 1L;

	public abstract String getButtonImageUrl();
	
	public final URI getCallbackUri(String providerName) {
		String serverUrl = Cheeta.getInstance(SettingService.class).getSystemSetting().getServerUrl();
		try {
			return new URI(serverUrl + "/" + SsoProcessPage.MOUNT_PATH + "/" 
					+ SsoProcessPage.STAGE_CALLBACK + "/" + providerName);
		} catch (URISyntaxException e) {
			throw new RuntimeException(e);
		}	
	}
	
	public abstract SsoAuthenticated handleAuthResponse(String providerName);
	
	public abstract String buildAuthUrl(String providerName);

}

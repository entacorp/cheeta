package io.cheeta.server.mail;

import io.cheeta.commons.utils.ExplicitException;
import io.cheeta.server.Cheeta;
import io.cheeta.server.service.AlertService;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.util.oauth.OAuthTokenService;
import io.cheeta.server.util.oauth.RefreshTokenAccessor;
import org.unbescape.html.HtmlEscape;

public class OAuthAccessToken implements MailCredential {

	private static final long serialVersionUID = 1L;

	private final String tokenEndpoint;
	
	private final String clientId;
	
	private final String clientSecret;
	
	private final RefreshTokenAccessor refreshTokenAccessor;
	
	public OAuthAccessToken(String tokenEndpoint, String clientId, String clientSecret,
							RefreshTokenAccessor refreshTokenAccessor) {
		this.tokenEndpoint = tokenEndpoint;
		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.refreshTokenAccessor = refreshTokenAccessor;
	}

	@Override
	public String getValue() {
		try {
			return Cheeta.getInstance(OAuthTokenService.class).getAccessToken(tokenEndpoint, clientId, clientSecret, refreshTokenAccessor);
		} catch (ExplicitException e) {
			if (SecurityUtils.isAnonymous() || SecurityUtils.isSystem())
				Cheeta.getInstance(AlertService.class).alert("Failed to get access token of mail server",
						HtmlEscape.escapeHtml5(e.getMessage()), true);
			throw e;
		}
	}

}

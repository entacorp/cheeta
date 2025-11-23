package io.cheeta.server.web.page.admin.authenticator;

import java.io.Serializable;

import io.cheeta.server.model.support.administration.authenticator.Authenticator;
import io.cheeta.server.annotation.Editable;

@Editable
public class AuthenticatorBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private Authenticator authenticator;

	@Editable(placeholder="No external password authenticator")
	public Authenticator getAuthenticator() {
		return authenticator;
	}

	public void setAuthenticator(Authenticator authenticator) {
		this.authenticator = authenticator;
	}

}

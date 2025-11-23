package io.cheeta.server.web.page.user.twofactorauthentication;

import io.cheeta.commons.utils.ExplicitException;
import io.cheeta.server.model.User;
import io.cheeta.server.web.component.user.twofactorauthentication.TwoFactorAuthenticationStatusPanel;
import io.cheeta.server.web.page.user.UserPage;

import org.apache.wicket.request.mapper.parameter.PageParameters;

public class UserTwoFactorAuthenticationPage extends UserPage {

	public UserTwoFactorAuthenticationPage(PageParameters params) {
		super(params);
		if (getUser().isServiceAccount() || getUser().isDisabled())
			throw new IllegalStateException();
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();

		if (!getUser().isEnforce2FA())
			throw new ExplicitException("Two-factor authentication not enabled");

		add(new TwoFactorAuthenticationStatusPanel("content") {
			@Override
			protected User getUser() {
				return UserTwoFactorAuthenticationPage.this.getUser();
			}
		});
	}

}

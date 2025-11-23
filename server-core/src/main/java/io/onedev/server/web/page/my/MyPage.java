package io.cheeta.server.web.page.my;

import org.apache.wicket.RestartResponseAtInterceptPageException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.model.User;
import io.cheeta.server.web.page.layout.LayoutPage;
import io.cheeta.server.web.page.security.LoginPage;
import io.cheeta.server.web.util.UserAware;

public abstract class MyPage extends LayoutPage implements UserAware {
	
	public MyPage(PageParameters params) {
		super(params);
		if (getUser() == null) 
			throw new RestartResponseAtInterceptPageException(LoginPage.class);
	}

	@Override
	public User getUser() {
		return getLoginUser();
	}

	@Override
	protected String getPageTitle() {
		return "My - " + Cheeta.getInstance(SettingService.class).getBrandingSetting().getName();
	}
	
}

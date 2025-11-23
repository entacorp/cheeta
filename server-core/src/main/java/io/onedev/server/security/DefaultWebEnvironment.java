package io.cheeta.server.security;

import org.apache.shiro.ShiroException;
import org.apache.shiro.util.Initializable;
import org.apache.shiro.web.config.ShiroFilterConfiguration;
import org.apache.shiro.web.filter.mgt.FilterChainResolver;
import org.apache.shiro.web.mgt.WebSecurityManager;

import io.cheeta.server.Cheeta;

public class DefaultWebEnvironment extends org.apache.shiro.web.env.DefaultWebEnvironment implements Initializable {

	@Override
	public void init() throws ShiroException {
		setWebSecurityManager(Cheeta.getInstance(WebSecurityManager.class));
		setFilterChainResolver(Cheeta.getInstance(FilterChainResolver.class));
		setShiroFilterConfiguration(Cheeta.getInstance(ShiroFilterConfiguration.class));
	}

}

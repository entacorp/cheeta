package io.cheeta.server.security;

import org.apache.shiro.web.filter.mgt.FilterChainManager;

import io.cheeta.commons.loader.ExtensionPoint;

@ExtensionPoint
public interface FilterChainConfigurator {
	void configure(FilterChainManager filterChainManager);
}

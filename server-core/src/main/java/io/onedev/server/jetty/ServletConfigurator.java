package io.cheeta.server.jetty;

import org.eclipse.jetty.servlet.ServletContextHandler;

import io.cheeta.commons.loader.ExtensionPoint;

@ExtensionPoint
public interface ServletConfigurator {
	void configure(ServletContextHandler context);
}

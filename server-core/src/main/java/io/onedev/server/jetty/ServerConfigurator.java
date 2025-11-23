package io.cheeta.server.jetty;

import org.eclipse.jetty.server.Server;

import io.cheeta.commons.loader.ExtensionPoint;

@ExtensionPoint
public interface ServerConfigurator {
	void configure(Server server);
}

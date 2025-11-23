package io.cheeta.server.web;

import org.apache.wicket.protocol.http.WebApplication;

import io.cheeta.commons.loader.ExtensionPoint;

@ExtensionPoint
public interface WebApplicationConfigurator {

	void configure(WebApplication application);
	
}

package io.cheeta.server.web;

import java.util.Collection;

import io.cheeta.commons.loader.ExtensionPoint;

@ExtensionPoint
public interface ResourcePackScopeContribution {
	Collection<Class<?>> getResourcePackScopes();
}

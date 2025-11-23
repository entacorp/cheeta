package io.cheeta.server.replica;

import io.cheeta.commons.loader.ExtensionPoint;

@ExtensionPoint
public interface BuildStorageSyncer {
	
	void sync(Long projectId, Long buildNumber, String activeServer);
	
}

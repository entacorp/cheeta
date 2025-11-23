package io.cheeta.server.service;

import io.cheeta.server.model.Pack;
import io.cheeta.server.model.PackBlob;
import io.cheeta.server.model.PackBlobReference;

public interface PackBlobReferenceService extends EntityService<PackBlobReference> {

	void create(PackBlobReference blobReference);
	
	void createIfNotExist(Pack pack, PackBlob packBlob);
		
}

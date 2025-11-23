package io.cheeta.server.service;

import io.cheeta.server.model.Pack;
import io.cheeta.server.model.PackLabel;

import java.util.Collection;

public interface PackLabelService extends EntityLabelService<PackLabel> {

	void create(PackLabel packLabel);
	
	void populateLabels(Collection<Pack> packs);
	
}

package io.cheeta.server.search.entity.pack;

import io.cheeta.server.model.Pack;
import io.cheeta.server.model.User;
import io.cheeta.server.util.criteria.Criteria;

public abstract class PublishedByCriteria extends Criteria<Pack> {

	public abstract User getUser();
	
}

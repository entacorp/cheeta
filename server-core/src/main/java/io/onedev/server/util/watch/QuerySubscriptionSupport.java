package io.cheeta.server.util.watch;

import java.util.LinkedHashSet;

import io.cheeta.server.model.support.NamedQuery;

public abstract class QuerySubscriptionSupport<T extends NamedQuery> {

	public abstract LinkedHashSet<String> getQuerySubscriptions();
	
}

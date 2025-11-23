package io.cheeta.server.model.support;

import javax.persistence.MappedSuperclass;

import io.cheeta.server.model.AbstractEntity;
import io.cheeta.server.model.User;

@MappedSuperclass
public abstract class EntityWatch extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	public abstract AbstractEntity getEntity();

	public abstract User getUser();

	public abstract boolean isWatching();
	
	public abstract void setWatching(boolean watching);

}

package io.cheeta.server.event.entity;

import io.cheeta.server.event.Event;
import io.cheeta.server.model.AbstractEntity;

public abstract class EntityEvent extends Event {
	
	private final AbstractEntity entity;
	
	public EntityEvent(AbstractEntity entity) {
		this.entity = entity;
	}

	public AbstractEntity getEntity() {
		return entity;
	}
	
}

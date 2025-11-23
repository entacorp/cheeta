package io.cheeta.server.event.entity;

import io.cheeta.server.model.AbstractEntity;

public class EntityRemoved extends EntityEvent {
	
	public EntityRemoved(AbstractEntity entity) {
		super(entity);
	}

}

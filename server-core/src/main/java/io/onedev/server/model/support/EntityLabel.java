package io.cheeta.server.model.support;

import javax.persistence.MappedSuperclass;

import io.cheeta.server.model.AbstractEntity;
import io.cheeta.server.model.LabelSpec;

@MappedSuperclass
public abstract class EntityLabel extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	public abstract AbstractEntity getEntity();
	
	public abstract LabelSpec getSpec();

}

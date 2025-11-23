package io.cheeta.server.model.support;

import javax.persistence.MappedSuperclass;

import io.cheeta.server.model.AbstractEntity;
import io.cheeta.server.model.Project;

@MappedSuperclass
public abstract class ProjectBelonging extends AbstractEntity {

	private static final long serialVersionUID = 1L;

	public abstract Project getProject();
		
}

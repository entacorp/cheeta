package io.cheeta.server.service.impl;

import javax.inject.Singleton;

import com.google.common.base.Preconditions;

import io.cheeta.server.model.AgentLastUsedDate;
import io.cheeta.server.persistence.annotation.Transactional;
import io.cheeta.server.service.AgentLastUsedDateService;

@Singleton
public class DefaultAgentLastUsedDateService extends BaseEntityService<AgentLastUsedDate> implements AgentLastUsedDateService {

	@Transactional
	@Override
	public void create(AgentLastUsedDate lastUsedDate) {
		Preconditions.checkState(lastUsedDate.isNew());
		dao.persist(lastUsedDate);
	}
	
}

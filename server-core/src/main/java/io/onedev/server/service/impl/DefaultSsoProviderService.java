package io.cheeta.server.service.impl;

import java.util.List;

import javax.inject.Singleton;

import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import io.cheeta.server.model.SsoProvider;
import io.cheeta.server.persistence.annotation.Sessional;
import io.cheeta.server.persistence.annotation.Transactional;
import io.cheeta.server.persistence.dao.EntityCriteria;
import io.cheeta.server.service.SsoProviderService;

@Singleton
public class DefaultSsoProviderService extends BaseEntityService<SsoProvider> implements SsoProviderService {

	@Transactional
	@Override
	public void createOrUpdate(SsoProvider ssoProvider) {
		dao.persist(ssoProvider);
	}
	
	@Sessional
    @Override
    public SsoProvider find(String name) {
		var criteria = EntityCriteria.of(SsoProvider.class);
		criteria.add(Restrictions.eq(SsoProvider.PROP_NAME, name));
		return dao.find(criteria);
    }

	@Sessional
	public List<SsoProvider> query() {
		var criteria = EntityCriteria.of(SsoProvider.class);
		criteria.addOrder(Order.asc(SsoProvider.PROP_NAME));
		criteria.setCacheable(true);
		return query(criteria);
	}
	
}

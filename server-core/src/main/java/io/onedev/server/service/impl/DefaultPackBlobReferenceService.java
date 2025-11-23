package io.cheeta.server.service.impl;

import static io.cheeta.server.model.PackBlobReference.PROP_PACK;
import static io.cheeta.server.model.PackBlobReference.PROP_PACK_BLOB;

import java.io.ObjectStreamException;
import java.io.Serializable;

import javax.inject.Singleton;

import org.hibernate.criterion.Restrictions;

import com.google.common.base.Preconditions;

import io.cheeta.commons.loader.ManagedSerializedForm;
import io.cheeta.server.model.Pack;
import io.cheeta.server.model.PackBlob;
import io.cheeta.server.model.PackBlobReference;
import io.cheeta.server.persistence.annotation.Transactional;
import io.cheeta.server.service.PackBlobReferenceService;

@Singleton
public class DefaultPackBlobReferenceService extends BaseEntityService<PackBlobReference>
		implements PackBlobReferenceService, Serializable {

	@Transactional
	@Override
	public void create(PackBlobReference blobReference) {
		Preconditions.checkState(blobReference.isNew());
		dao.persist(blobReference);
	}

	@Transactional
	@Override
	public void createIfNotExist(Pack pack, PackBlob packBlob) {
		var criteria = newCriteria();
		criteria.add(Restrictions.eq(PROP_PACK, pack));
		criteria.add(Restrictions.eq(PROP_PACK_BLOB, packBlob));
		if (find(criteria) == null) {
			var blobReference = new PackBlobReference();
			blobReference.setPack(pack);
			blobReference.setPackBlob(packBlob);
			dao.persist(blobReference);
		}
	}
	
	public Object writeReplace() throws ObjectStreamException {
		return new ManagedSerializedForm(PackBlobReferenceService.class);
	}
	
}

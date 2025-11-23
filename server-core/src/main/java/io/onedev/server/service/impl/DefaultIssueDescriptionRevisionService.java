package io.cheeta.server.service.impl;

import javax.inject.Singleton;

import com.google.common.base.Preconditions;

import io.cheeta.server.model.IssueDescriptionRevision;
import io.cheeta.server.persistence.annotation.Transactional;
import io.cheeta.server.service.IssueDescriptionRevisionService;

@Singleton
public class DefaultIssueDescriptionRevisionService extends BaseEntityService<IssueDescriptionRevision>
        implements IssueDescriptionRevisionService {

    @Transactional
    @Override
    public void create(IssueDescriptionRevision revision) {
        Preconditions.checkArgument(revision.isNew());
        dao.persist(revision);
    }
    
} 
package io.cheeta.server.service.impl;

import javax.inject.Singleton;

import com.google.common.base.Preconditions;

import io.cheeta.server.model.PullRequestDescriptionRevision;
import io.cheeta.server.persistence.annotation.Transactional;
import io.cheeta.server.service.PullRequestDescriptionRevisionService;

@Singleton
public class DefaultPullRequestDescriptionRevisionService extends BaseEntityService<PullRequestDescriptionRevision>
        implements PullRequestDescriptionRevisionService {

    @Transactional
    @Override
    public void create(PullRequestDescriptionRevision revision) {
        Preconditions.checkArgument(revision.isNew());
        dao.persist(revision);
    }
    
} 
package io.cheeta.server.service.impl;

import javax.inject.Singleton;

import com.google.common.base.Preconditions;

import io.cheeta.server.model.PullRequestCommentRevision;
import io.cheeta.server.persistence.annotation.Transactional;
import io.cheeta.server.service.PullRequestCommentRevisionService;

@Singleton
public class DefaultPullRequestCommentRevisionService extends BaseEntityService<PullRequestCommentRevision>
        implements PullRequestCommentRevisionService {

    @Transactional
    @Override
    public void create(PullRequestCommentRevision revision) {
        Preconditions.checkArgument(revision.isNew());
        dao.persist(revision);
    }
    
} 
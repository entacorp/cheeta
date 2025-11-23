package io.cheeta.server.service.impl;

import javax.inject.Singleton;

import com.google.common.base.Preconditions;

import io.cheeta.server.model.IssueCommentRevision;
import io.cheeta.server.persistence.annotation.Transactional;
import io.cheeta.server.service.IssueCommentRevisionService;

@Singleton
public class DefaultIssueCommentRevisionService extends BaseEntityService<IssueCommentRevision>
        implements IssueCommentRevisionService {

    @Transactional
    @Override
    public void create(IssueCommentRevision revision) {
        Preconditions.checkArgument(revision.isNew());
        dao.persist(revision);
    }
} 
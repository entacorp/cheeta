package io.cheeta.server.service;

import io.cheeta.server.model.PullRequestCommentRevision;

public interface PullRequestCommentRevisionService extends EntityService<PullRequestCommentRevision> {

    void create(PullRequestCommentRevision revision);
    
}

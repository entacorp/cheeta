package io.cheeta.server.service;

import io.cheeta.server.model.IssueCommentRevision;

public interface IssueCommentRevisionService extends EntityService<IssueCommentRevision> {

    void create(IssueCommentRevision revision);
    
}

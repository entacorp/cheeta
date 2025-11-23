package io.cheeta.server.service;

import io.cheeta.server.model.IssueDescriptionRevision;

public interface IssueDescriptionRevisionService extends EntityService<IssueDescriptionRevision> {

    void create(IssueDescriptionRevision revision);
		
}

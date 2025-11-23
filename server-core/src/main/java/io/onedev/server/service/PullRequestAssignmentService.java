package io.cheeta.server.service;

import io.cheeta.server.model.PullRequestAssignment;

public interface PullRequestAssignmentService extends EntityService<PullRequestAssignment> {

    void create(PullRequestAssignment assignment);
}

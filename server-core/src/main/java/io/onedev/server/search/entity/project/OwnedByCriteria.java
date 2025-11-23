package io.cheeta.server.search.entity.project;

import org.jspecify.annotations.Nullable;

import io.cheeta.server.model.Project;
import io.cheeta.server.model.User;
import io.cheeta.server.util.criteria.Criteria;

public abstract class OwnedByCriteria extends Criteria<Project> {

    @Nullable
    public abstract User getUser();

}

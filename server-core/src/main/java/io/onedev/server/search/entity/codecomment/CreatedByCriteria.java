package io.cheeta.server.search.entity.codecomment;

import io.cheeta.server.model.CodeComment;
import io.cheeta.server.model.User;
import io.cheeta.server.util.criteria.Criteria;

public abstract class CreatedByCriteria extends Criteria<CodeComment> {

	public abstract User getUser();
	
}

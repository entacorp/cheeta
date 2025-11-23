package io.cheeta.server.service;

import io.cheeta.server.model.CodeCommentQueryPersonalization;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.User;

public interface CodeCommentQueryPersonalizationService extends EntityService<CodeCommentQueryPersonalization> {
	
	CodeCommentQueryPersonalization find(Project project, User user);

    void createOrUpdate(CodeCommentQueryPersonalization personalization);

}

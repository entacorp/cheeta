package io.cheeta.server.service;

import io.cheeta.server.model.CodeComment;
import io.cheeta.server.model.CodeCommentMention;
import io.cheeta.server.model.User;

public interface CodeCommentMentionService extends EntityService<CodeCommentMention> {

	void mention(CodeComment comment, User user);

}

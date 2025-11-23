package io.cheeta.server.service.impl;

import javax.inject.Singleton;

import io.cheeta.server.model.CodeComment;
import io.cheeta.server.model.CodeCommentMention;
import io.cheeta.server.model.User;
import io.cheeta.server.service.CodeCommentMentionService;

@Singleton
public class DefaultCodeCommentMentionService extends BaseEntityService<CodeCommentMention>
		implements CodeCommentMentionService {

	@Override
	public void mention(CodeComment comment, User user) {
		if (comment.getMentions().stream().noneMatch(it->it.getUser().equals(user))) {
			CodeCommentMention mention = new CodeCommentMention();
			mention.setComment(comment);
			mention.setUser(user);
			dao.persist(mention);
		}
	}

}

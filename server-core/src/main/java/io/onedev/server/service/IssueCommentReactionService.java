package io.cheeta.server.service;

import io.cheeta.server.model.IssueComment;
import io.cheeta.server.model.IssueCommentReaction;
import io.cheeta.server.model.User;

public interface IssueCommentReactionService extends EntityService<IssueCommentReaction> {

    void create(IssueCommentReaction reaction);

    void toggleEmoji(User user, IssueComment comment, String emoji);

} 
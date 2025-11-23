package io.cheeta.server.service;

import io.cheeta.server.model.PullRequestComment;
import io.cheeta.server.model.PullRequestCommentReaction;
import io.cheeta.server.model.User;

public interface PullRequestCommentReactionService extends EntityService<PullRequestCommentReaction> {

    void create(PullRequestCommentReaction reaction);

    void toggleEmoji(User user, PullRequestComment comment, String emoji);

} 
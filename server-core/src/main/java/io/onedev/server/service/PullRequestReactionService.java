package io.cheeta.server.service;

import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.PullRequestReaction;
import io.cheeta.server.model.User;

public interface PullRequestReactionService extends EntityService<PullRequestReaction> {

    void create(PullRequestReaction reaction);

    void toggleEmoji(User user, PullRequest request, String emoji);

} 
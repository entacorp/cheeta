package io.cheeta.server.service;

import io.cheeta.server.model.IssueReaction;
import io.cheeta.server.model.Issue;
import io.cheeta.server.model.User;

public interface IssueReactionService extends EntityService<IssueReaction> {

    void create(IssueReaction reaction);
    
    void toggleEmoji(User user, Issue issue, String emoji);

}

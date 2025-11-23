package io.cheeta.server.service;

import java.util.List;

import org.jspecify.annotations.Nullable;

import org.eclipse.jgit.lib.ObjectId;

import io.cheeta.server.model.PendingSuggestionApply;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.User;

public interface PendingSuggestionApplyService extends EntityService<PendingSuggestionApply> {
	
	ObjectId apply(User user, PullRequest request, String commitMessage);

	void discard(@Nullable User user, PullRequest request);

	List<PendingSuggestionApply> query(User user, PullRequest request);

    void create(PendingSuggestionApply pendingApply);
}
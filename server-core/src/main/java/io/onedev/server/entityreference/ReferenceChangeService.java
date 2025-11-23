package io.cheeta.server.entityreference;

import org.jspecify.annotations.Nullable;

import io.cheeta.server.model.CodeComment;
import io.cheeta.server.model.Issue;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.model.User;

public interface ReferenceChangeService {

	void addReferenceChange(User user, Issue issue, @Nullable String markdown);
	
	void addReferenceChange(User user, PullRequest request, @Nullable String markdown);
	
	void addReferenceChange(User user, CodeComment comment, @Nullable String markdown);
	
}

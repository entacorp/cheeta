package io.cheeta.server.service;

import io.cheeta.server.model.ReviewedDiff;
import io.cheeta.server.model.User;

import java.util.Map;

public interface ReviewedDiffService extends EntityService<ReviewedDiff> {
	
	Map<String, ReviewedDiff> query(User user, String oldCommitHash, String newCommitHash);

	void createOrUpdate(ReviewedDiff status);
	
}

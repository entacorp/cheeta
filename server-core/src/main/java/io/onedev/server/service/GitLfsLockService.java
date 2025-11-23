package io.cheeta.server.service;

import io.cheeta.server.model.GitLfsLock;

public interface GitLfsLockService extends EntityService<GitLfsLock> {

	GitLfsLock find(String path);

    void create(GitLfsLock lock);
	
}

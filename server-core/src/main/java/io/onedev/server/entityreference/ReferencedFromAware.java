package io.cheeta.server.entityreference;

import org.jspecify.annotations.Nullable;

import io.cheeta.server.model.AbstractEntity;
import io.cheeta.server.model.CodeComment;
import io.cheeta.server.model.Issue;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.security.SecurityUtils;

public interface ReferencedFromAware<T extends AbstractEntity> {

	@Nullable
	T getReferencedFrom();
	
	public static boolean canDisplay(ReferencedFromAware<?> referencedFromAware) {
		AbstractEntity referencedFrom = referencedFromAware.getReferencedFrom();
		if (referencedFrom instanceof Issue) 
			return SecurityUtils.canAccessIssue((Issue) referencedFrom);
		else if (referencedFrom instanceof PullRequest) 
			return SecurityUtils.canReadCode(((PullRequest) referencedFrom).getProject());
		else if (referencedFrom instanceof CodeComment) 
			return SecurityUtils.canReadCode(((CodeComment) referencedFrom).getProject());
		else 
			return referencedFrom != null; 
	}
}

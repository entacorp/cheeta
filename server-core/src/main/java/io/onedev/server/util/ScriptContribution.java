package io.cheeta.server.util;

import io.cheeta.commons.loader.ExtensionPoint;
import io.cheeta.server.Cheeta;
import io.cheeta.server.service.EmailAddressService;
import io.cheeta.server.model.Build;
import io.cheeta.server.model.EmailAddress;
import io.cheeta.server.model.User;
import io.cheeta.server.model.support.administration.GroovyScript;
import org.eclipse.jgit.revwalk.RevCommit;

import org.jspecify.annotations.Nullable;

/**
 * Use abstract class instead of interface here as otherwise groovy can not invoke static methods defined here
 * @author robin
 *
 */
@ExtensionPoint
public abstract class ScriptContribution {
	
	public abstract GroovyScript getScript();
	
	@Nullable
	public static String determineBuildFailureInvestigator() {
		Build build = Build.get();
		if (build != null) {
			RevCommit commit = Build.get().getProject().getRevCommit(build.getCommitId(), true);
			EmailAddressService emailAddressService = Cheeta.getInstance(EmailAddressService.class);
			EmailAddress emailAddress = emailAddressService.findByPersonIdent(commit.getCommitterIdent());
			if (emailAddress != null && emailAddress.isVerified())
				return emailAddress.getOwner().getName();
			else
				return null;
		} else {
			return null;
		}
	}

	@Nullable
	public static Long getBuildNumber() {
		Build build = Build.get();
		if (build != null)
			return build.getNumber();
		else
			return null;
	}

	@Nullable
	public static String getCurrentUser() {
		User user = User.get();
		if (user != null)
			return user.getName();
		else
			return null;
	}
	
}

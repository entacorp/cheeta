package io.cheeta.server.search.commit;

import java.io.Serializable;

import io.cheeta.commons.codeassist.AntlrUtils;
import io.cheeta.commons.utils.StringUtils;
import io.cheeta.server.event.project.RefUpdated;
import io.cheeta.server.git.command.RevListOptions;
import io.cheeta.server.model.Project;

public abstract class CommitCriteria implements Serializable {

	private static final long serialVersionUID = 1L;

	public abstract void fill(Project project, RevListOptions options);
	
	public abstract boolean matches(RefUpdated event);
	
	public static String getRuleName(int rule) {
		return AntlrUtils.getLexerRuleName(CommitQueryLexer.ruleNames, rule).replace(' ', '-');
	}
	
	public static String parens(String value) {
		return "(" + StringUtils.escape(value, "()") + ")";
	}
	
}

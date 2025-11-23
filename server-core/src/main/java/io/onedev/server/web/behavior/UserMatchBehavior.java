package io.cheeta.server.web.behavior;

import io.cheeta.commons.codeassist.FenceAware;
import io.cheeta.commons.codeassist.InputSuggestion;
import io.cheeta.commons.codeassist.grammar.LexerRuleRefElementSpec;
import io.cheeta.commons.codeassist.parser.TerminalExpect;
import io.cheeta.server.util.usermatch.UserMatchParser;
import io.cheeta.server.web.behavior.inputassist.ANTLRAssistBehavior;
import io.cheeta.server.web.util.SuggestionUtils;

import java.util.List;

import static io.cheeta.server.util.usermatch.UserMatchLexer.GROUP;
import static io.cheeta.server.util.usermatch.UserMatchLexer.USER;
import static io.cheeta.server.web.translation.Translation._T;

public class UserMatchBehavior extends ANTLRAssistBehavior {
	
	public UserMatchBehavior() {
		super(UserMatchParser.class, "userMatch", false);
	}

	@Override
	protected List<InputSuggestion> suggest(TerminalExpect terminalExpect) {
		if (terminalExpect.getElementSpec() instanceof LexerRuleRefElementSpec) {
			LexerRuleRefElementSpec spec = (LexerRuleRefElementSpec) terminalExpect.getElementSpec();
			if (spec.getRuleName().equals("Value")) {
				return new FenceAware(codeAssist.getGrammar(), '(', ')') {

					@Override
					protected List<InputSuggestion> match(String matchWith) {
						int tokenType = terminalExpect.getState().getLastMatchedToken().getType();
						switch (tokenType) {
							case USER:
								return SuggestionUtils.suggestUsers(matchWith);
							case GROUP:
								return SuggestionUtils.suggestGroups(matchWith); 
							default: 
								return null;
						} 
					}

					@Override
					protected String getFencingDescription() {
						return _T("value needs to be enclosed in parenthesis");
					}
					
				}.suggest(terminalExpect);
			}
		} 
		return null;
	}

}

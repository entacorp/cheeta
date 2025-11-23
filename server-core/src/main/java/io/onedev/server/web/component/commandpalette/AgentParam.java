package io.cheeta.server.web.component.commandpalette;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.AgentService;
import io.cheeta.server.model.Agent;
import io.cheeta.server.search.entity.agent.*;
import io.cheeta.server.util.criteria.Criteria;
import io.cheeta.server.util.criteria.OrCriteria;
import io.cheeta.server.web.page.admin.buildsetting.agent.AgentDetailPage;

public class AgentParam extends ParamSegment {

	private static final long serialVersionUID = 1L;
	
	public AgentParam(boolean optional) {
		super(AgentDetailPage.PARAM_AGENT, optional);
	}
	
	@Override
	public Map<String, String> suggest(String matchWith, Map<String, String> paramValues, int count) {
		Map<String, String> suggestions = new LinkedHashMap<>();
		AgentService agentService = Cheeta.getInstance(AgentService.class);
		AgentQuery query;
		if (matchWith.length() == 0) {
			query = new AgentQuery();
		} else {
			List<Criteria<Agent>> criterias = new ArrayList<>();
			criterias.add(new NameCriteria("*" + matchWith + "*", AgentQueryLexer.Is));
			criterias.add(new OsCriteria("*" + matchWith + "*", AgentQueryLexer.Is));
			criterias.add(new OsVersionCriteria("*" + matchWith + "*", AgentQueryLexer.Is));
			criterias.add(new OsArchCriteria("*" + matchWith + "*", AgentQueryLexer.Is));
			query = new AgentQuery(new OrCriteria<Agent>(criterias));
		}
		
		for (Agent agent: agentService.query(query, 0, count))
			suggestions.put(agent.getName(), agent.getName());
		
		return suggestions;
	}

	@Override
	public boolean isExactMatch(String matchWith, Map<String, String> paramValues) {
		AgentService agentService = Cheeta.getInstance(AgentService.class);
		return agentService.findByName(matchWith) != null; 
	}
		
}

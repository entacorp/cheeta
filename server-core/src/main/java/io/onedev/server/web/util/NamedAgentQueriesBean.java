package io.cheeta.server.web.util;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.cheeta.server.model.support.NamedAgentQuery;
import io.cheeta.server.web.component.savedquery.NamedQueriesBean;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.OmitName;

@Editable
public class NamedAgentQueriesBean extends NamedQueriesBean<NamedAgentQuery> {

	private static final long serialVersionUID = 1L;

	private List<NamedAgentQuery> queries = new ArrayList<>();

	@NotNull
	@Editable
	@OmitName
	public List<NamedAgentQuery> getQueries() {
		return queries;
	}

	public void setQueries(List<NamedAgentQuery> queries) {
		this.queries = queries;
	}

}
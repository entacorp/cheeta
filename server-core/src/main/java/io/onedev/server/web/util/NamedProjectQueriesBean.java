package io.cheeta.server.web.util;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.cheeta.server.model.support.NamedProjectQuery;
import io.cheeta.server.web.component.savedquery.NamedQueriesBean;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.OmitName;

@Editable
public class NamedProjectQueriesBean extends NamedQueriesBean<NamedProjectQuery> {

	private static final long serialVersionUID = 1L;

	private List<NamedProjectQuery> queries = new ArrayList<>();

	@NotNull
	@Editable
	@OmitName
	public List<NamedProjectQuery> getQueries() {
		return queries;
	}

	public void setQueries(List<NamedProjectQuery> queries) {
		this.queries = queries;
	}

}
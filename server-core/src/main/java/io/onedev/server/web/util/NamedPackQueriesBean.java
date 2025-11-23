package io.cheeta.server.web.util;

import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.OmitName;
import io.cheeta.server.model.support.pack.NamedPackQuery;
import io.cheeta.server.web.component.savedquery.NamedQueriesBean;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Editable
public class NamedPackQueriesBean extends NamedQueriesBean<NamedPackQuery> {

	private static final long serialVersionUID = 1L;

	private List<NamedPackQuery> queries = new ArrayList<>();

	@NotNull
	@Editable
	@OmitName
	public List<NamedPackQuery> getQueries() {
		return queries;
	}

	public void setQueries(List<NamedPackQuery> queries) {
		this.queries = queries;
	}

}
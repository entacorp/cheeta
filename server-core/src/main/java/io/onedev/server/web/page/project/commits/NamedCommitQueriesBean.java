package io.cheeta.server.web.page.project.commits;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.cheeta.server.model.support.NamedCommitQuery;
import io.cheeta.server.web.component.savedquery.NamedQueriesBean;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.OmitName;

@Editable
public class NamedCommitQueriesBean extends NamedQueriesBean<NamedCommitQuery> {

	private static final long serialVersionUID = 1L;

	private List<NamedCommitQuery> queries = new ArrayList<>();

	@Override
	@NotNull
	@Editable
	@OmitName
	public List<NamedCommitQuery> getQueries() {
		return queries;
	}

	@Override
	public void setQueries(List<NamedCommitQuery> queries) {
		this.queries = queries;
	}

}
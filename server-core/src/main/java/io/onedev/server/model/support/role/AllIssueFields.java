package io.cheeta.server.model.support.role;

import java.util.Collection;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.annotation.Editable;

@Editable(order=100, name="All")
public class AllIssueFields implements IssueFieldSet {

	private static final long serialVersionUID = 1L;

	@Override
	public Collection<String> getIncludeFields() {
		return Cheeta.getInstance(SettingService.class).getIssueSetting().getFieldNames();
	}

}

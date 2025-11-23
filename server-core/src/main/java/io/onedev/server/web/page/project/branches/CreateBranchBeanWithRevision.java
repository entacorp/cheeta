package io.cheeta.server.web.page.project.branches;

import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.OmitName;
import io.cheeta.server.annotation.RevisionChoice;
import io.cheeta.server.web.component.branch.create.CreateBranchBean;

import javax.validation.constraints.NotEmpty;

@Editable
public class CreateBranchBeanWithRevision extends CreateBranchBean {

	private static final long serialVersionUID = 1L;

	private String revision;

	@Editable(order=1000)
	@RevisionChoice
	@NotEmpty(message="Please choose revision to create branch from")
	@OmitName
	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}
	
}

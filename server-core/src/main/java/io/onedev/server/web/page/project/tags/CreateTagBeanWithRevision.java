package io.cheeta.server.web.page.project.tags;

import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.OmitName;
import io.cheeta.server.annotation.RevisionChoice;
import io.cheeta.server.web.component.createtag.CreateTagBean;

import javax.validation.constraints.NotEmpty;

@Editable
public class CreateTagBeanWithRevision extends CreateTagBean {

	private static final long serialVersionUID = 1L;

	private String revision;

	@Editable(order=1000, name="Revision")
	@RevisionChoice
	@NotEmpty(message = "Please select revision to create tag from")
	@OmitName
	public String getRevision() {
		return revision;
	}

	public void setRevision(String revision) {
		this.revision = revision;
	}
	
}

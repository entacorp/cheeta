package io.cheeta.server.web.page.project.builds.detail;

import java.io.Serializable;

import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.Markdown;
import io.cheeta.server.annotation.OmitName;

@Editable(name="Build Description")
public class DescriptionBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String value;

	@Editable
	@Markdown
	@OmitName
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}

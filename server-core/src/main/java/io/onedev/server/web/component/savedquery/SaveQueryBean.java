package io.cheeta.server.web.component.savedquery;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.OmitName;

@Editable
public class SaveQueryBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	
	@Editable(description="Specify name of the saved query")
	@NotEmpty
	@OmitName
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
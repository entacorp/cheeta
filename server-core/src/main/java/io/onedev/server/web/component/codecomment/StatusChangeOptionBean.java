package io.cheeta.server.web.component.codecomment;

import java.io.Serializable;

import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.Multiline;
import io.cheeta.server.annotation.OmitName;

@Editable(name="Confirm your action")
public class StatusChangeOptionBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String note;

	@Editable(placeholder="Leave a note")
	@Multiline
	@OmitName
	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
}

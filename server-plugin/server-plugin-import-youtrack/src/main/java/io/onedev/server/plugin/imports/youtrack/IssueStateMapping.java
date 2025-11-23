package io.cheeta.server.plugin.imports.youtrack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.model.support.administration.GlobalIssueSetting;
import io.cheeta.server.model.support.issue.StateSpec;
import io.cheeta.server.annotation.ChoiceProvider;
import io.cheeta.server.annotation.Editable;

@Editable
public class IssueStateMapping implements Serializable {

	private static final long serialVersionUID = 1L;

	private String youTrackIssueState;
	
	private String oneDevIssueState;

	@Editable(order=100, name="YouTrack Issue State")
	@NotEmpty
	public String getYouTrackIssueState() {
		return youTrackIssueState;
	}

	public void setYouTrackIssueState(String youTrackIssueState) {
		this.youTrackIssueState = youTrackIssueState;
	}

	@Editable(order=200, name="Cheeta Issue State")
	@ChoiceProvider("getCheetaIssueStateChoices")
	@NotEmpty
	public String getCheetaIssueState() {
		return oneDevIssueState;
	}

	public void setCheetaIssueState(String oneDevIssueState) {
		this.oneDevIssueState = oneDevIssueState;
	}

	@SuppressWarnings("unused")
	private static List<String> getCheetaIssueStateChoices() {
		List<String> choices = new ArrayList<>();
		GlobalIssueSetting issueSetting = Cheeta.getInstance(SettingService.class).getIssueSetting();
		for (StateSpec state: issueSetting.getStateSpecs()) 
			choices.add(state.getName());
		return choices;
	}
	
}

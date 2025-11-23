package io.cheeta.server.plugin.imports.jiracloud;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.constraints.NotEmpty;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.model.support.administration.GlobalIssueSetting;
import io.cheeta.server.annotation.ChoiceProvider;
import io.cheeta.server.annotation.Editable;

@Editable
public class IssueStatusMapping implements Serializable {

	private static final long serialVersionUID = 1L;

	private String jiraIssueStatus;
	
	private String oneDevIssueState;

	@Editable(order=100, name="JIRA Issue Status")
	@NotEmpty
	public String getJiraIssueStatus() {
		return jiraIssueStatus;
	}

	public void setJiraIssueStatus(String jiraIssueStatus) {
		this.jiraIssueStatus = jiraIssueStatus;
	}

	@Editable(order=200, name="Cheeta Issue State", description="Cheeta Issue State")
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
		GlobalIssueSetting issueSetting = Cheeta.getInstance(SettingService.class).getIssueSetting();
		return issueSetting.getStateSpecs().stream().map(it->it.getName()).collect(Collectors.toList());
	}
	
}

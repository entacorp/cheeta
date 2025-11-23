package io.cheeta.server.web.component.issue.list;

import static io.cheeta.server.web.translation.Translation._T;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.cheeta.server.Cheeta;
import io.cheeta.server.annotation.ChoiceProvider;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.service.LinkSpecService;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.model.Issue;
import io.cheeta.server.model.IssueSchedule;
import io.cheeta.server.model.LinkSpec;

@Editable
public class FieldsAndLinksBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<String> fields;
	
	private List<String> links;

	@Editable(order=100, name="Display Fields", placeholder="Not displaying any fields", 
			description="Specify fields to be displayed in the issue list")
	@ChoiceProvider(value="getFieldChoices", displayNames="getFieldDisplayNames")
	public List<String> getFields() {
		return fields;
	}

	public void setFields(List<String> fields) {
		this.fields = fields;
	}
	
	@SuppressWarnings("unused")
	private static List<String> getFieldChoices() {
		List<String> choices = new ArrayList<>();
		choices.add(Issue.NAME_STATE);
		for (String fieldName: Cheeta.getInstance(SettingService.class).getIssueSetting().getFieldNames())
			choices.add(fieldName);
		choices.add(IssueSchedule.NAME_ITERATION);
		return choices;
	}

	@SuppressWarnings("unused")
	private static Map<String, String> getFieldDisplayNames() {
		Map<String, String> displayNames = new HashMap<>();
		displayNames.put(Issue.NAME_STATE, _T("State"));
		for (String fieldName: Cheeta.getInstance(SettingService.class).getIssueSetting().getFieldNames())
			displayNames.put(fieldName, fieldName);
		displayNames.put(IssueSchedule.NAME_ITERATION, _T("Iteration"));
		return displayNames;
	}
	
	@Editable(order=200, name="Display Links", placeholder="Not displaying any links", 
			description="Specify links to be displayed in the issue list")
	@ChoiceProvider("getLinkChoices")
	public List<String> getLinks() {
		return links;
	}

	public void setLinks(List<String> links) {
		this.links = links;
	}

	@SuppressWarnings("unused")
	private static List<String> getLinkChoices() {
		List<String> choices = new ArrayList<>();
		for (LinkSpec linkSpec: Cheeta.getInstance(LinkSpecService.class).queryAndSort()) {
			choices.add(linkSpec.getName());
			if (linkSpec.getOpposite() != null)
				choices.add(linkSpec.getOpposite().getName());
		}
		return choices;
	}
	
}

package io.cheeta.server.plugin.imports.youtrack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.model.support.administration.GlobalIssueSetting;
import io.cheeta.server.buildspecmodel.inputspec.InputSpec;
import io.cheeta.server.model.support.issue.field.spec.FieldSpec;
import io.cheeta.server.annotation.ChoiceProvider;
import io.cheeta.server.annotation.Editable;

@Editable
public class IssueTagMapping implements Serializable {

	private static final long serialVersionUID = 1L;

	private String youTrackIssueTag;
	
	private String oneDevIssueField;

	@Editable(order=100, name="YouTrack Issue Tag")
	@NotEmpty
	public String getYouTrackIssueTag() {
		return youTrackIssueTag;
	}

	public void setYouTrackIssueTag(String youTrackIssueTag) {
		this.youTrackIssueTag = youTrackIssueTag;
	}

	@Editable(order=200, name="Cheeta Issue Field", description="Specify a custom field of Enum type")
	@ChoiceProvider("getCheetaIssueFieldChoices")
	@NotEmpty
	public String getCheetaIssueField() {
		return oneDevIssueField;
	}

	public void setCheetaIssueField(String oneDevIssueField) {
		this.oneDevIssueField = oneDevIssueField;
	}

	@SuppressWarnings("unused")
	private static List<String> getCheetaIssueFieldChoices() {
		List<String> choices = new ArrayList<>();
		GlobalIssueSetting issueSetting = Cheeta.getInstance(SettingService.class).getIssueSetting();
		for (FieldSpec field: issueSetting.getFieldSpecs()) {
			if (field.getType().equals(InputSpec.ENUMERATION)) {
				for (String value: field.getPossibleValues()) 
					choices.add(field.getName() + "::" + value);
			}
		}
		return choices;
	}
	
}

package io.cheeta.server.web.component.suggestionapply;

import static io.cheeta.server.web.translation.Translation._T;

import java.io.Serializable;

import javax.validation.constraints.NotEmpty;

import io.cheeta.server.annotation.BranchChoice;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.Multiline;

@Editable(name="Commit Suggestion")
public class SuggestionApplyBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private String branch;
	
	private String commitMessage = _T("Apply suggested change from code comment");

	@Editable(order=100, description="Specify branch to commit suggested change")
	@BranchChoice
	@NotEmpty
	public String getBranch() {
		return branch;
	}

	public void setBranch(String branch) {
		this.branch = branch;
	}

	@Editable(order=200)
	@Multiline
	@NotEmpty
	public String getCommitMessage() {
		return commitMessage;
	}

	public void setCommitMessage(String commitMessage) {
		this.commitMessage = commitMessage;
	}
	
}

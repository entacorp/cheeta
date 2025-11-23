package io.cheeta.server.git.location;

import javax.validation.constraints.NotEmpty;

import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.OmitName;

@Editable(order=200, name="Use Specified Git")
public class SpecifiedGit extends GitLocation {

	private static final long serialVersionUID = 1L;
	
	private String gitPath;
	
	@Editable(description="Specify path to git executable, for instance: <tt>/usr/bin/git</tt>")
	@OmitName
	@NotEmpty
	public String getGitPath() {
		return gitPath;
	}

	public void setGitPath(String gitPath) {
		this.gitPath = gitPath;
	}

	@Override
	public String getExecutable() {
		return gitPath;
	}

}

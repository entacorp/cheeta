package io.cheeta.server.plugin.imports.gitlab;

import java.util.Collection;
import java.util.List;

import javax.validation.constraints.Size;

import io.cheeta.server.annotation.ChoiceProvider;
import io.cheeta.server.annotation.DependsOn;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.ProjectChoice;
import io.cheeta.server.util.ComponentContext;
import io.cheeta.server.util.EditContext;
import io.cheeta.server.web.editable.BeanEditor;

@Editable
public class ImportProjects extends ImportGroup {

	private static final long serialVersionUID = 1L;

	private String parentCheetaProject;
	
	private boolean all;
	
	private boolean includeForks;
	
	private List<String> gitLabProjects;

	@Editable(order=200, name="Parent Cheeta Project", description = "Optionally specify a Cheeta project " +
			"to be used as parent of imported projects. Leave empty to import as root projects")
	@ProjectChoice
	public String getParentCheetaProject() {
		return parentCheetaProject;
	}

	public void setParentCheetaProject(String parentCheetaProject) {
		this.parentCheetaProject = parentCheetaProject;
	}
	
	@Editable(order=300, name="Import All Projects")
	public boolean isAll() {
		return all;
	}

	public void setAll(boolean all) {
		this.all = all;
	}

	@Editable(order=400, description="Whether or not to import forked GitLab projects")
	@DependsOn(property="all")
	public boolean isIncludeForks() {
		return includeForks;
	}

	public void setIncludeForks(boolean includeForks) {
		this.includeForks = includeForks;
	}

	@Editable(order=500, name="GitLab Projects to Import")
	@ChoiceProvider("getGitLabProjectChoices")
	@DependsOn(property="all", value="false")
	@Size(min=1, message="At least one project should be selected")
	public List<String> getGitLabProjects() {
		return gitLabProjects;
	}

	public void setGitLabProjects(List<String> gitLabProjects) {
		this.gitLabProjects = gitLabProjects;
	}

	@SuppressWarnings("unused")
	private static List<String> getGitLabProjectChoices() {
		BeanEditor editor = ComponentContext.get().getComponent().findParent(BeanEditor.class);
		ImportProjects projects = (ImportProjects) editor.getModelObject();
		String groupId = (String) EditContext.get().getInputValue("groupId");
		return projects.server.listProjects(groupId, true);
	}
	
	public Collection<String> getImportProjects() {
		if (isAll()) 
			return server.listProjects(getGroupId(), isIncludeForks());
		else
			return getGitLabProjects();	
	} 
	
}

package io.cheeta.server.plugin.imports.jiracloud;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidatorContext;
import javax.validation.constraints.Size;

import io.cheeta.server.annotation.ChoiceProvider;
import io.cheeta.server.annotation.ClassValidating;
import io.cheeta.server.annotation.DependsOn;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.security.permission.CreateChildren;
import io.cheeta.server.util.ComponentContext;
import io.cheeta.server.validation.Validatable;
import io.cheeta.server.web.editable.BeanEditor;

@Editable
@ClassValidating
public class ImportProjects implements Serializable, Validatable {

	private static final long serialVersionUID = 1L;
	
	ImportServer server;

	private String parentCheetaProject;

	private boolean all;

	private List<String> jiraProjects;

	@Editable(order=200, name="Parent Cheeta Project", description = "Optionally specify a Cheeta project " +
			"to be used as parent of imported projects. Leave empty to import as root projects")
	@ChoiceProvider("getParentCheetaProjectChoices")
	public String getParentCheetaProject() {
		return parentCheetaProject;
	}

	public void setParentCheetaProject(String parentCheetaProject) {
		this.parentCheetaProject = parentCheetaProject;
	}

	@SuppressWarnings("unused")
	private static List<String> getParentCheetaProjectChoices() {
		return SecurityUtils.getAuthorizedProjects(new CreateChildren()).stream()
				.map(it->it.getPath()).sorted().collect(Collectors.toList());
	}

	@Editable(order=300, name="Import All Projects")
	public boolean isAll() {
		return all;
	}

	public void setAll(boolean all) {
		this.all = all;
	}

	@Editable(order=500, name="JIRA Projects to Import")
	@ChoiceProvider("getJiraProjectChoices")
	@DependsOn(property="all", value="false")
	@Size(min=1, message="At least one project should be selected")
	public List<String> getJiraProjects() {
		return jiraProjects;
	}

	public void setJiraProjects(List<String> jiraProjects) {
		this.jiraProjects = jiraProjects;
	}

	@SuppressWarnings("unused")
	private static List<String> getJiraProjectChoices() {
		BeanEditor editor = ComponentContext.get().getComponent().findParent(BeanEditor.class);
		ImportProjects projects = (ImportProjects) editor.getModelObject();
		return projects.server.listProjects();
	}

	public Collection<String> getImportProjects() {
		if (isAll())
			return server.listProjects();
		else
			return getJiraProjects();
	}

	@Override
	public boolean isValid(ConstraintValidatorContext context) {
		if (parentCheetaProject == null && !SecurityUtils.canCreateRootProjects()) {
			context.disableDefaultConstraintViolation();
			var errorMessage = "No permission to import as root projects, please specify parent project";
			context.buildConstraintViolationWithTemplate(errorMessage)
					.addPropertyNode("parentCheetaProject")
					.addConstraintViolation();
			return false;
		} else {
			return true;
		}
	}

}

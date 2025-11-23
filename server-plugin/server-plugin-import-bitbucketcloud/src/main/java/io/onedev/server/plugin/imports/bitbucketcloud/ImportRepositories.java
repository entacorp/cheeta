package io.cheeta.server.plugin.imports.bitbucketcloud;

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
import io.cheeta.server.util.EditContext;
import io.cheeta.server.validation.Validatable;
import io.cheeta.server.web.editable.BeanEditor;

@Editable
@ClassValidating
public class ImportRepositories extends ImportWorkspace implements Validatable {

	private static final long serialVersionUID = 1L;

	private String parentCheetaProject;

	private boolean all;

	private boolean includeForks;

	private List<String> bitbucketRepositories;

	@Editable(order=200, name="Parent Cheeta Project", description = "Optionally specify a Cheeta project " +
			"to be used as parent of imported repositories. Leave empty to import as root projects")
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

	@Editable(order=300, name="Import All Repositories")
	public boolean isAll() {
		return all;
	}

	public void setAll(boolean all) {
		this.all = all;
	}

	@Editable(order=400, description="Whether or not to import forked Bitbucket repositories")
	@DependsOn(property="all")
	public boolean isIncludeForks() {
		return includeForks;
	}

	public void setIncludeForks(boolean includeForks) {
		this.includeForks = includeForks;
	}

	@Editable(order=500, name="Bitbucket Repositories to Import")
	@ChoiceProvider("getBitbucketRepositoryChoices")
	@DependsOn(property="all", value="false")
	@Size(min=1, message="At least one repository should be selected")
	public List<String> getBitbucketRepositories() {
		return bitbucketRepositories;
	}

	public void setBitbucketRepositories(List<String> bitbucketRepositories) {
		this.bitbucketRepositories = bitbucketRepositories;
	}

	@SuppressWarnings("unused")
	private static List<String> getBitbucketRepositoryChoices() {
		BeanEditor editor = ComponentContext.get().getComponent().findParent(BeanEditor.class);
		ImportRepositories repositories = (ImportRepositories) editor.getModelObject();
		String workspace = (String) EditContext.get().getInputValue("workspace");
		return repositories.server.listRepositories(workspace, true);
	}

	public Collection<String> getImportRepositories() {
		if (isAll())
			return server.listRepositories(getWorkspace(), isIncludeForks());
		else
			return getBitbucketRepositories();
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

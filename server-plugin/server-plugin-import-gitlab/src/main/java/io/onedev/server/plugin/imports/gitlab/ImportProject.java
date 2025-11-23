package io.cheeta.server.plugin.imports.gitlab;

import io.cheeta.server.annotation.ChoiceProvider;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.util.ComponentContext;
import io.cheeta.server.util.EditContext;
import io.cheeta.server.web.editable.BeanEditor;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Editable
public class ImportProject extends ImportGroup {

	private static final long serialVersionUID = 1L;
	
	private String project;

	@Editable(order=200, name="GitLab Project", description="Select project to import from")
	@ChoiceProvider("getProjectChoices")
	@NotEmpty
	public String getProject() {
		return project;
	}

	public void setProject(String project) {
		this.project = project;
	}
	
	@SuppressWarnings("unused")
	private static List<String> getProjectChoices() {
		BeanEditor editor = ComponentContext.get().getComponent().findParent(BeanEditor.class);
		ImportProject project = (ImportProject) editor.getModelObject();
		String groupId = (String) EditContext.get().getInputValue("groupId");
		return project.server.listProjects(groupId, true);
	}
	
}

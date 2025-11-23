package io.cheeta.server.plugin.imports.gitlab;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.cheeta.server.annotation.ChoiceProvider;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.util.ComponentContext;
import io.cheeta.server.web.editable.BeanEditor;

@Editable
public class ImportGroup implements Serializable {

	private static final long serialVersionUID = 1L;
	
	ImportServer server;
	
	private String groupId;
		
	@Editable(order=100, name="GitLab Group", description="Specify group to import from. "
			+ "Leave empty to import from projects under current account")
	@ChoiceProvider(value="getGroupChoices", displayNames="getGroupDisplayNames")
	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}
	
	private static Map<String, String> getGroupDisplayNames() {
		BeanEditor editor = ComponentContext.get().getComponent().findParent(BeanEditor.class);
		ImportGroup setting = (ImportGroup) editor.getModelObject();
		return setting.server.listGroups();
	}
	
	@SuppressWarnings("unused")
	private static List<String> getGroupChoices() {
		return new ArrayList<>(getGroupDisplayNames().keySet());
	}
	
}

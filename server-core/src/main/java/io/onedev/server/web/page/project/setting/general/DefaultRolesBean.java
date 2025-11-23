package io.cheeta.server.web.page.project.setting.general;

import static java.util.stream.Collectors.toList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import io.cheeta.server.Cheeta;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.RoleChoice;
import io.cheeta.server.service.RoleService;
import io.cheeta.server.model.Role;

@Editable
public class DefaultRolesBean implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<String> roleNames = new ArrayList<>();

	@Editable(name="Default Roles", placeholder="Inherit from parent", rootPlaceholder ="No default roles", description="Default roles affect default " +
			"permissions granted to everyone in the system. The actual default permissions will be <b class='text-warning'>all permissions</b> contained in default roles of this " +
			"project and all its parent projects")
	@RoleChoice
	public List<String> getRoleNames() {
		return roleNames;
	}

	public void setRoleNames(List<String> roleNames) {
		this.roleNames = roleNames;
	}
	
	public void setRoles(List<Role> roles) {
		roleNames = roles.stream().map(Role::getName).sorted().collect(toList());
	}
	
	public List<Role> getRoles() {
		return roleNames.stream().map(Cheeta.getInstance(RoleService.class)::find).collect(toList());
	}
	
}

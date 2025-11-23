package io.cheeta.server.web.component.commandpalette;

import java.util.LinkedHashMap;
import java.util.Map;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.RoleService;
import io.cheeta.server.model.Role;
import io.cheeta.server.web.page.admin.rolemanagement.RoleDetailPage;

public class RoleParam extends ParamSegment {

	private static final long serialVersionUID = 1L;
	
	public RoleParam(boolean optional) {
		super(RoleDetailPage.PARAM_ROLE, optional);
	}
	
	@Override
	public Map<String, String> suggest(String matchWith, 
			Map<String, String> paramValues, int count) {
		Map<String, String> suggestions = new LinkedHashMap<>();
		if (matchWith.length() == 0)
			matchWith = null;
		for (Role role: Cheeta.getInstance(RoleService.class).query(matchWith, 0, count))
			suggestions.put(role.getName(), String.valueOf(role.getId()));
		return suggestions;
	}

	@Override
	public boolean isExactMatch(String matchWith, Map<String, String> paramValues) {
		RoleService roleService = Cheeta.getInstance(RoleService.class);
		try {
			Long roleId = Long.valueOf(matchWith);
			if (roleService.get(roleId) != null) 
				return true;
		} catch (NumberFormatException e) {
		}
		return false;
	}
		
}

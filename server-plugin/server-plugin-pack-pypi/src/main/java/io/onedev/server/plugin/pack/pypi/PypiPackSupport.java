package io.cheeta.server.plugin.pack.pypi;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.PackService;
import io.cheeta.server.model.Pack;
import io.cheeta.server.model.Project;
import io.cheeta.server.pack.PackSupport;
import org.apache.wicket.Component;
import org.apache.wicket.model.LoadableDetachableModel;

public class PypiPackSupport implements PackSupport {
	
	public static final String TYPE = "PyPi";
	
	@Override
	public int getOrder() {
		return 300;
	}

	@Override
	public String getPackType() {
		return TYPE;
	}

	@Override
	public String getPackIcon() {
		return "python";
	}

	@Override
	public String getReference(Pack pack, boolean withProject) {
		var reference = pack.getName() + "-" + pack.getVersion();
		if (withProject)
			reference = pack.getProject().getPath() + ":" + reference;
		return reference;
	}

	@Override
	public Component renderContent(String componentId, Pack pack) {
		var packId = pack.getId();
		return new PypiPackPanel(componentId, new LoadableDetachableModel<>() {
			@Override
			protected Pack load() {
				return Cheeta.getInstance(PackService.class).load(packId);
			}

		});
	}

	@Override
	public Component renderHelp(String componentId, Project project) {
		return new PypiHelpPanel(componentId, project.getPath());
	}
	
}

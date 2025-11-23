package io.cheeta.server.plugin.pack.nuget;

import io.cheeta.commons.loader.AbstractPluginModule;
import io.cheeta.server.pack.PackHandler;
import io.cheeta.server.pack.PackSupport;

/**
 * NOTE: Do not forget to rename moduleClass property defined in the pom if you've renamed this class.
 *
 */
public class NugetModule extends AbstractPluginModule {

	@Override
	protected void configure() {
		super.configure();

		bind(NugetPackHandler.class);
		contribute(PackHandler.class, NugetPackHandler.class);
		contribute(PackSupport.class, new NugetPackSupport());
	}

}

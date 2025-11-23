package io.cheeta.server.plugin.pack.helm;

import io.cheeta.commons.loader.AbstractPluginModule;
import io.cheeta.server.pack.PackHandler;
import io.cheeta.server.pack.PackSupport;

/**
 * NOTE: Do not forget to rename moduleClass property defined in the pom if you've renamed this class.
 *
 */
public class HelmModule extends AbstractPluginModule {

	@Override
	protected void configure() {
		super.configure();

		bind(HelmPackHandler.class);
		contribute(PackHandler.class, HelmPackHandler.class);
		contribute(PackSupport.class, new HelmPackSupport());
	}

}

package io.cheeta.server.plugin.pack.gem;

import io.cheeta.commons.loader.AbstractPluginModule;
import io.cheeta.server.pack.PackHandler;
import io.cheeta.server.pack.PackSupport;

/**
 * NOTE: Do not forget to rename moduleClass property defined in the pom if you've renamed this class.
 *
 */
public class GemModule extends AbstractPluginModule {

	@Override
	protected void configure() {
		super.configure();

		bind(GemPackHandler.class);
		contribute(PackHandler.class, GemPackHandler.class);
		contribute(PackSupport.class, new GemPackSupport());
	}

}

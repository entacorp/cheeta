package io.cheeta.server.plugin.pack.npm;

import io.cheeta.commons.loader.AbstractPluginModule;
import io.cheeta.server.pack.PackHandler;
import io.cheeta.server.pack.PackSupport;

/**
 * NOTE: Do not forget to rename moduleClass property defined in the pom if you've renamed this class.
 *
 */
public class NpmModule extends AbstractPluginModule {

	@Override
	protected void configure() {
		super.configure();

		bind(NpmPackHandler.class);
		contribute(PackHandler.class, NpmPackHandler.class);
		contribute(PackSupport.class, new NpmPackSupport());
	}

}

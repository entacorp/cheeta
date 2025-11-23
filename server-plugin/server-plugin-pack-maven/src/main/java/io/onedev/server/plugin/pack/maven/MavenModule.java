package io.cheeta.server.plugin.pack.maven;

import io.cheeta.commons.loader.AbstractPluginModule;
import io.cheeta.server.pack.PackHandler;
import io.cheeta.server.pack.PackSupport;

/**
 * NOTE: Do not forget to rename moduleClass property defined in the pom if you've renamed this class.
 *
 */
public class MavenModule extends AbstractPluginModule {

	@Override
	protected void configure() {
		super.configure();

		bind(MavenPackHandler.class);
		contribute(PackHandler.class, MavenPackHandler.class);
		contribute(PackSupport.class, new MavenPackSupport());
	}

}

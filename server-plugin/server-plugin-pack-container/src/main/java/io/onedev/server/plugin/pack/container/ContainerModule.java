package io.cheeta.server.plugin.pack.container;

import org.eclipse.jetty.servlet.ServletHolder;

import io.cheeta.commons.loader.AbstractPluginModule;
import io.cheeta.server.Cheeta;
import io.cheeta.server.jetty.ServletConfigurator;
import io.cheeta.server.pack.PackSupport;
import io.cheeta.server.security.FilterChainConfigurator;

/**
 * NOTE: Do not forget to rename moduleClass property defined in the pom if you've renamed this class.
 *
 */
public class ContainerModule extends AbstractPluginModule {

	@Override
	protected void configure() {
		super.configure();

		contribute(PackSupport.class, new ContainerPackSupport());
		bind(ContainerServlet.class);

		contribute(ServletConfigurator.class, context -> {
			context.addServlet(
					new ServletHolder(Cheeta.getInstance(ContainerServlet.class)),
					ContainerServlet.PATH + "/*");
		});

		bind(ContainerAuthenticationFilter.class);
		contribute(FilterChainConfigurator.class, filterChainManager -> {
			filterChainManager.addFilter("containerAuthc",
					Cheeta.getInstance(ContainerAuthenticationFilter.class));
			filterChainManager.createChain(
					ContainerServlet.PATH + "/**",
					"noSessionCreation, containerAuthc");
		});		
	}

}

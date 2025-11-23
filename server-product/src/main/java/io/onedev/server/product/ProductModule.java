package io.cheeta.server.product;

import io.cheeta.commons.loader.AbstractPluginModule;
import io.cheeta.server.ServerConfig;
import io.cheeta.server.jetty.ServerConfigurator;
import io.cheeta.server.jetty.ServletConfigurator;
import io.cheeta.server.persistence.HibernateConfig;
import io.cheeta.server.util.ProjectNameReservation;

import java.util.HashSet;
import java.util.Set;

import static io.cheeta.commons.bootstrap.Bootstrap.installDir;
import static io.cheeta.server.Cheeta.getAssetsDir;

public class ProductModule extends AbstractPluginModule {

    @Override
	protected void configure() {
		super.configure();
		
		bind(HibernateConfig.class).toInstance(new HibernateConfig(installDir));
		bind(ServerConfig.class).toInstance(new ServerConfig(installDir));

		contribute(ServerConfigurator.class, ProductConfigurator.class);
		contribute(ServletConfigurator.class, ProductServletConfigurator.class);
		
		contribute(ProjectNameReservation.class, () -> {
			Set<String> reserved = new HashSet<>();
			for (var file : getAssetsDir().listFiles())
				reserved.add(file.getName());
			return reserved;
		});
		
	}

}

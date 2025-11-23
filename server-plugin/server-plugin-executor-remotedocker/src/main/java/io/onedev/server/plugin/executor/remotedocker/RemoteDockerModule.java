package io.cheeta.server.plugin.executor.remotedocker;

import java.util.Collection;

import com.google.common.collect.Sets;

import io.cheeta.commons.loader.AbstractPluginModule;
import io.cheeta.commons.loader.ImplementationProvider;
import io.cheeta.server.model.support.administration.jobexecutor.JobExecutor;

/**
 * NOTE: Do not forget to rename moduleClass property defined in the pom if you've renamed this class.
 *
 */
public class RemoteDockerModule extends AbstractPluginModule {

	@Override
	protected void configure() {
		super.configure();
		
		// put your guice bindings here
		contribute(ImplementationProvider.class, new ImplementationProvider() {

			@Override
			public Class<?> getAbstractClass() {
				return JobExecutor.class;
			}

			@Override
			public Collection<Class<?>> getImplementations() {
				return Sets.newHashSet(RemoteDockerExecutor.class);
			}
			
		});
	}

}

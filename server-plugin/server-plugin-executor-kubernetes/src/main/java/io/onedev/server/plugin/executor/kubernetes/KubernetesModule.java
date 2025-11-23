package io.cheeta.server.plugin.executor.kubernetes;

import java.util.Collection;

import org.glassfish.jersey.server.ResourceConfig;

import com.google.common.collect.Sets;

import io.cheeta.commons.loader.AbstractPluginModule;
import io.cheeta.commons.loader.ImplementationProvider;
import io.cheeta.server.Cheeta;
import io.cheeta.server.buildspec.job.JobExecutorDiscoverer;
import io.cheeta.server.model.support.administration.jobexecutor.JobExecutor;
import io.cheeta.server.rest.JerseyConfigurator;

/**
 * NOTE: Do not forget to rename moduleClass property defined in the pom if you've renamed this class.
 *
 */
public class KubernetesModule extends AbstractPluginModule {

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
				return Sets.newHashSet(KubernetesExecutor.class);
			}
			
		});
		
		contribute(JobExecutorDiscoverer.class, new JobExecutorDiscoverer() {
			
			@Override
			public JobExecutor discover() {
				if (Cheeta.getK8sService() != null)
					return new KubernetesExecutor();
				else
					return null;
			}

			@Override
			public int getOrder() {
				return KubernetesExecutor.ORDER;
			}
			
		});
		
		contribute(JerseyConfigurator.class, new JerseyConfigurator() {
			
			@Override
			public void configure(ResourceConfig resourceConfig) {
				resourceConfig.register(KubernetesResource.class);
			}
			
		});
		
	}

}

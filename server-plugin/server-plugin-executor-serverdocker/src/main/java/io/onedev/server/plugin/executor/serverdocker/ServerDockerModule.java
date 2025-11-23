package io.cheeta.server.plugin.executor.serverdocker;

import java.io.File;
import java.util.Collection;

import org.apache.commons.lang.SystemUtils;

import com.google.common.collect.Sets;

import io.cheeta.commons.loader.AbstractPluginModule;
import io.cheeta.commons.loader.ImplementationProvider;
import io.cheeta.commons.utils.ExceptionUtils;
import io.cheeta.commons.utils.command.Commandline;
import io.cheeta.commons.utils.command.LineConsumer;
import io.cheeta.server.Cheeta;
import io.cheeta.server.buildspec.job.JobExecutorDiscoverer;
import io.cheeta.server.model.support.administration.jobexecutor.JobExecutor;

/**
 * NOTE: Do not forget to rename moduleClass property defined in the pom if you've renamed this class.
 *
 */
public class ServerDockerModule extends AbstractPluginModule {

	@Override
	protected void configure() {
		super.configure();
		
		// put your guice bindings here

		if (Cheeta.getK8sService() == null) {
			contribute(ImplementationProvider.class, new ImplementationProvider() {

				@Override
				public Class<?> getAbstractClass() {
					return JobExecutor.class;
				}

				@Override
				public Collection<Class<?>> getImplementations() {
					return Sets.newHashSet(ServerDockerExecutor.class);
				}
				
			});
			
			contribute(JobExecutorDiscoverer.class, new JobExecutorDiscoverer() {

				@Override
				public JobExecutor discover() {
					Commandline docker;
					if (SystemUtils.IS_OS_MAC_OSX && new File("/usr/local/bin/docker").exists())
						docker = new Commandline("/usr/local/bin/docker");
					else
						docker = new Commandline("docker");
					
					docker.addArgs("version");
					try {
						docker.execute(new LineConsumer() {
				
							@Override
							public void consume(String line) {
							}
							
						}, new LineConsumer() {
				
							@Override
							public void consume(String line) {
							}
							
						}).checkReturnCode();
						
						return new ServerDockerExecutor();
					} catch (Exception e) {
						if (ExceptionUtils.find(e, InterruptedException.class) != null)
							throw ExceptionUtils.unchecked(e);
						else
							return null;
					}
				}

				@Override
				public int getOrder() {
					return ServerDockerExecutor.ORDER;
				}
				
			});
		}
	}

}

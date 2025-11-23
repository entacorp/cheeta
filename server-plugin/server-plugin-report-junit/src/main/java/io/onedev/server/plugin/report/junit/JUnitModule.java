package io.cheeta.server.plugin.report.junit;

import java.util.Collection;

import com.google.common.collect.Sets;

import io.cheeta.commons.loader.AbstractPluginModule;
import io.cheeta.commons.loader.ImplementationProvider;
import io.cheeta.server.buildspec.step.PublishReportStep;

/**
 * NOTE: Do not forget to rename moduleClass property defined in the pom if you've renamed this class.
 *
 */
public class JUnitModule extends AbstractPluginModule {

	@Override
	protected void configure() {
		super.configure();
		
		contribute(ImplementationProvider.class, new ImplementationProvider() {

			@Override
			public Class<?> getAbstractClass() {
				return PublishReportStep.class;
			}
			
			@Override
			public Collection<Class<?>> getImplementations() {
				return Sets.newHashSet(PublishJUnitReportStep.class);
			}
			
		});
	}

}

package io.cheeta.server.plugin.report.html;

import com.google.common.collect.Sets;
import io.cheeta.commons.loader.AbstractPluginModule;
import io.cheeta.commons.loader.ImplementationProvider;
import io.cheeta.server.Cheeta;
import io.cheeta.server.buildspec.step.PublishReportStep;
import io.cheeta.server.cluster.ClusterTask;
import io.cheeta.server.service.BuildService;
import io.cheeta.server.service.ProjectService;
import io.cheeta.server.model.Build;
import io.cheeta.server.replica.BuildStorageSyncer;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.web.WebApplicationConfigurator;
import io.cheeta.server.web.mapper.BaseResourceMapper;
import io.cheeta.server.web.mapper.ProjectPageMapper;
import io.cheeta.server.web.page.project.builds.detail.BuildTab;
import io.cheeta.server.web.page.project.builds.detail.BuildTabContribution;
import io.cheeta.server.web.page.project.builds.detail.report.BuildReportTab;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static io.cheeta.commons.utils.LockUtils.read;
import static io.cheeta.server.model.Build.getProjectRelativeDirPath;
import static io.cheeta.server.plugin.report.html.PublishHtmlReportStep.CATEGORY;
import static io.cheeta.server.plugin.report.html.PublishHtmlReportStep.getReportLockName;
import static io.cheeta.server.util.DirectoryVersionUtils.isVersionFile;

/**
 * NOTE: Do not forget to rename moduleClass property defined in the pom if you've renamed this class.
 *
 */
public class HtmlModule extends AbstractPluginModule {

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
				return Sets.newHashSet(PublishHtmlReportStep.class);
			}

		});
		
		contribute(BuildTabContribution.class, new BuildTabContribution() {
			
			@Override
			public List<BuildTab> getTabs(Build build) {
				Long projectId = build.getProject().getId();
				Long buildNumber = build.getNumber();
				
				return getProjectService().runOnActiveServer(projectId, new GetBuildTabs(projectId, buildNumber)).stream()
						.filter(it->SecurityUtils.canAccessReport(build, it.getTitle()))
						.collect(Collectors.toList());
			}
			
			@Override
			public int getOrder() {
				return 100;
			}
			
		});
		
		contribute(WebApplicationConfigurator.class, application -> {
			application.mount(new ProjectPageMapper("${project}/~builds/${build}/html/${report}", HtmlReportPage.class));
			application.mount(new BaseResourceMapper("~downloads/projects/${project}/builds/${build}/html/${report}",
					new HtmlReportDownloadResourceReference()));
		});		
		
		contribute(BuildStorageSyncer.class, ((projectId, buildNumber, activeServer) -> {
			getProjectService().syncDirectory(projectId, 
					getProjectRelativeDirPath(buildNumber) + "/" + CATEGORY,
					getReportLockName(projectId, buildNumber), activeServer);
		}));
	}
	
	private ProjectService getProjectService() {
		return Cheeta.getInstance(ProjectService.class);
	}
	
	private static class GetBuildTabs implements ClusterTask<List<BuildTab>> {

		private static final long serialVersionUID = 1L;
		
		private final Long projectId;
		
		private final Long buildNumber;
		
		public GetBuildTabs(Long projectId, Long buildNumber) {
			this.projectId = projectId;
			this.buildNumber = buildNumber;
		}

		@Override
		public List<BuildTab> call() {
			return read(getReportLockName(projectId, buildNumber), () -> {
				List<BuildTab> tabs = new ArrayList<>();
				File categoryDir = new File(Cheeta.getInstance(BuildService.class).getBuildDir(projectId, buildNumber), CATEGORY);
				if (categoryDir.exists()) {
					for (File reportDir: categoryDir.listFiles()) {
						if (!reportDir.isHidden() && !isVersionFile(reportDir)) {
							tabs.add(new BuildReportTab(reportDir.getName(), HtmlReportPage.class, null));
						}
					}
				}
				Collections.sort(tabs, (o1, o2) -> o1.getTitle().compareTo(o1.getTitle()));
				return tabs;
			});
		}
		
	}
}
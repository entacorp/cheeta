package io.cheeta.server.plugin.report.markdown;

import io.cheeta.commons.utils.FileUtils;
import io.cheeta.server.Cheeta;
import io.cheeta.server.cluster.ClusterTask;
import io.cheeta.server.service.BuildService;
import io.cheeta.server.service.ProjectService;
import io.cheeta.server.web.component.markdown.MarkdownViewer;
import io.cheeta.server.web.page.project.pullrequests.detail.PullRequestSummaryPart;
import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.model.Model;

import java.io.File;
import java.nio.charset.StandardCharsets;

public class PullRequestSummaryMarkdownPart extends PullRequestSummaryPart {

	private static final long serialVersionUID = 1L;
	
	private final Long projectId;
	
	private final Long buildNumber;
	
	public PullRequestSummaryMarkdownPart(Long projectId, Long buildNumber, String reportName) {
		super(reportName);
		this.projectId = projectId;
		this.buildNumber = buildNumber;
	}
	
	@Override
	public Component render(String componentId) {
		ProjectService projectService = Cheeta.getInstance(ProjectService.class);
		String markdown = projectService.runOnActiveServer(projectId, new ClusterTask<String>() {

			private static final long serialVersionUID = 1L;

			@Override
			public String call() throws Exception {
				File categoryDir = new File(Cheeta.getInstance(BuildService.class).getBuildDir(projectId, buildNumber), PublishPullRequestMarkdownReportStep.CATEGORY);
				File file = new File(new File(categoryDir, getReportName()), PublishPullRequestMarkdownReportStep.CONTENT);
				return FileUtils.readFileToString(file, StandardCharsets.UTF_8);
			}
			
		});
		return new MarkdownViewer(componentId, Model.of(markdown), null)
				.add(AttributeAppender.append("class", "mb-n3"));
	}

}

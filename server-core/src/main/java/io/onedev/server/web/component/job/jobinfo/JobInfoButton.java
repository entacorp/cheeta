package io.cheeta.server.web.component.job.jobinfo;

import static io.cheeta.server.web.translation.Translation._T;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.jspecify.annotations.Nullable;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.eclipse.jgit.lib.ObjectId;

import com.google.common.collect.Sets;

import io.cheeta.server.Cheeta;
import io.cheeta.server.buildspec.job.Job;
import io.cheeta.server.service.BuildService;
import io.cheeta.server.model.Build;
import io.cheeta.server.model.Build.Status;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.web.asset.pipelinebutton.PipelineButtonCssResourceReference;
import io.cheeta.server.web.behavior.ChangeObserver;
import io.cheeta.server.web.component.build.minilist.MiniBuildListPanel;
import io.cheeta.server.web.component.build.status.BuildStatusIcon;
import io.cheeta.server.web.component.floating.AlignPlacement;
import io.cheeta.server.web.component.floating.FloatingPanel;
import io.cheeta.server.web.component.job.RunJobLink;
import io.cheeta.server.web.component.link.DropdownLink;
import io.cheeta.server.web.page.project.builds.ProjectBuildsPage;
import io.cheeta.server.web.util.TextUtils;

public abstract class JobInfoButton extends Panel {

	public JobInfoButton(String id) {
		super(id);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		DropdownLink detailLink = new DropdownLink("detail", AlignPlacement.bottom(0), true, false) {

			@Override
			protected Component newContent(String id, FloatingPanel dropdown) {
				IModel<List<Build>> buildsModel = new LoadableDetachableModel<List<Build>>() {

					@Override
					protected List<Build> load() {
						BuildService buildService = Cheeta.getInstance(BuildService.class);
						List<Build> builds = new ArrayList<>(buildService.query(getProject(), getCommitId(), getJobName()));
						builds.sort(Comparator.comparing(Build::getNumber));
						return builds;
					}
					
				};						
				
				return new MiniBuildListPanel(id, buildsModel) {

					@Override
					protected Component newListLink(String componentId) {
						return new BookmarkablePageLink<Void>(componentId, ProjectBuildsPage.class, 
								ProjectBuildsPage.paramsOf(getProject(), Job.getBuildQuery(getCommitId(), getJobName(), null, null), 0)) {
							
							@Override
							protected void onConfigure() {
								super.onConfigure();
								setVisible(!getBuilds().isEmpty());
							}
							
						};
					}

					@Override
					protected Build getActiveBuild() {
						return JobInfoButton.this.getActiveBuild();
					}

				};
			}

			@Override
			protected void onComponentTag(ComponentTag tag) {
				super.onComponentTag(tag);
				
				String cssClasses = "btn btn-outline-secondary";
				Build.Status status = getProject().getCommitStatuses(getCommitId(), null, null).get(getJobName());
				String title;
				if (status != null) {
					String statusText = _T(TextUtils.getDisplayValue(status)).toLowerCase();
					if (status != Status.SUCCESSFUL)
						title = MessageFormat.format(_T("Some builds are {0}"), statusText); 
					else
						title = MessageFormat.format(_T("Builds are {0}"), statusText); 
				} else {
					title = _T("No builds");
					cssClasses += " no-builds";
				}
				tag.put("class", cssClasses);
				tag.put("title", title);
			}
			
		};
		detailLink.add(new BuildStatusIcon("status", new LoadableDetachableModel<Status>() {

			@Override
			protected Status load() {
				return getProject().getCommitStatuses(getCommitId(), null, null).get(getJobName());
			}
			
		}));
		
		detailLink.add(new Label("name", getJobName()));
		
		detailLink.add(new ChangeObserver() {
			
			@Override
			public Collection<String> findObservables() {
				return Sets.newHashSet(Build.getJobStatusChangeObservable(getProject().getId(), getCommitId().name(), getJobName()));
			}
			
		});
		detailLink.add(AttributeAppender.append("class", "justify-content-start text-nowrap"));
		detailLink.setOutputMarkupId(true);
		add(detailLink);
		
		String refName = getActiveBuild()!=null?getActiveBuild().getRefName():null;
		add(new RunJobLink("run", getCommitId(), getJobName(), refName) {

			@Override
			protected Project getProject() {
				return JobInfoButton.this.getProject();
			}

			@Override
			protected PullRequest getPullRequest() {
				return null;
			}
			
		});
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new PipelineButtonCssResourceReference()));
	}

	protected abstract Project getProject();
	
	protected abstract ObjectId getCommitId();
	
	protected abstract String getJobName();
	
	@Nullable
	protected Build getActiveBuild() {
		return null;
	}

}

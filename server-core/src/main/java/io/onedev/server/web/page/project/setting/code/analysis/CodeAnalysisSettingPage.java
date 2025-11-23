package io.cheeta.server.web.page.project.setting.code.analysis;

import static io.cheeta.server.web.translation.Translation._T;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import io.cheeta.server.Cheeta;
import io.cheeta.server.data.migration.VersionedXmlDoc;
import io.cheeta.server.service.ProjectService;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.support.CodeAnalysisSetting;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.web.component.link.ViewStateAwarePageLink;
import io.cheeta.server.web.editable.BeanContext;
import io.cheeta.server.web.page.project.ProjectPage;
import io.cheeta.server.web.page.project.dashboard.ProjectDashboardPage;
import io.cheeta.server.web.page.project.setting.ProjectSettingPage;

public class CodeAnalysisSettingPage extends ProjectSettingPage {

	public CodeAnalysisSettingPage(PageParameters params) {
		super(params);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		CodeAnalysisSetting bean = getProject().getCodeAnalysisSetting();
		var oldAuditContent = VersionedXmlDoc.fromBean(bean).toXML();
		Form<?> form = new Form<Void>("form") {

			@Override
			protected void onSubmit() {
				super.onSubmit();
				
				var newAuditContent = VersionedXmlDoc.fromBean(bean).toXML();
				getProject().setCodeAnalysisSetting(bean);
				Cheeta.getInstance(ProjectService.class).update(getProject());
				auditService.audit(getProject(), "changed code analysis settings", oldAuditContent, newAuditContent);
				setResponsePage(CodeAnalysisSettingPage.class, CodeAnalysisSettingPage.paramsOf(getProject()));
				Session.get().success(_T("Code analysis settings updated"));
			}
			
		};
		form.add(BeanContext.edit("editor", bean));
		
		add(form);
	}

	@Override
	protected Component newProjectTitle(String componentId) {
		return new Label(componentId, "<span class='text-truncate'>" + _T("Code Analysis Settings") + "</span>").setEscapeModelStrings(false);
	}

	@Override
	protected BookmarkablePageLink<Void> navToProject(String componentId, Project project) {
		if (SecurityUtils.canManageProject(project)) 
			return new ViewStateAwarePageLink<Void>(componentId, CodeAnalysisSettingPage.class, paramsOf(project.getId()));
		else 
			return new ViewStateAwarePageLink<Void>(componentId, ProjectDashboardPage.class, ProjectPage.paramsOf(project.getId()));
	}
	
}

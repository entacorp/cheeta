package io.cheeta.server.web.page.admin.performancesetting;

import static io.cheeta.server.web.translation.Translation._T;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import io.cheeta.server.Cheeta;
import io.cheeta.server.data.migration.VersionedXmlDoc;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.model.support.administration.PerformanceSetting;
import io.cheeta.server.web.editable.BeanContext;
import io.cheeta.server.web.page.admin.AdministrationPage;

public class PerformanceSettingPage extends AdministrationPage {

	public PerformanceSettingPage(PageParameters params) {
		super(params);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		PerformanceSetting performanceSetting = Cheeta.getInstance(SettingService.class).getPerformanceSetting();
		var oldAuditContent = VersionedXmlDoc.fromBean(performanceSetting).toXML();

		Form<?> form = new Form<Void>("form") {

			@Override
			protected void onSubmit() {
				super.onSubmit();
				var newAuditContent = VersionedXmlDoc.fromBean(performanceSetting).toXML();
				Cheeta.getInstance(SettingService.class).savePerformanceSetting(performanceSetting);
				auditService.audit(null, "changed performance settings", oldAuditContent, newAuditContent);				
				getSession().success(_T("Performance settings have been saved"));
				
				setResponsePage(PerformanceSettingPage.class);
			}
			
		};
		form.add(BeanContext.edit("editor", performanceSetting));
		
		add(form);
	}

	@Override
	protected Component newTopbarTitle(String componentId) {
		return new Label(componentId, _T("Performance Settings"));
	}

}

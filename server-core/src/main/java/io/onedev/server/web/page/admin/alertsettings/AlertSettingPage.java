package io.cheeta.server.web.page.admin.alertsettings;

import static io.cheeta.server.web.translation.Translation._T;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import io.cheeta.server.Cheeta;
import io.cheeta.server.data.migration.VersionedXmlDoc;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.web.editable.BeanContext;
import io.cheeta.server.web.page.admin.AdministrationPage;

public class AlertSettingPage extends AdministrationPage {

	public AlertSettingPage(PageParameters params) {
		super(params);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		var alertSetting = Cheeta.getInstance(SettingService.class).getAlertSetting();
		var oldAuditContent = VersionedXmlDoc.fromBean(alertSetting.getNotifyUsers()).toXML();

		Form<?> form = new Form<Void>("form") {

			@Override
			protected void onSubmit() {
				super.onSubmit();
				var newAuditContent = VersionedXmlDoc.fromBean(alertSetting.getNotifyUsers()).toXML();
				Cheeta.getInstance(SettingService.class).saveAlertSetting(alertSetting);
				auditService.audit(null, "changed alert settings", oldAuditContent, newAuditContent);
				getSession().success(_T("Alert settings have been updated"));
				
				setResponsePage(AlertSettingPage.class);
			}
			
		};
		form.add(BeanContext.edit("editor", alertSetting));
		
		add(form);
	}

	@Override
	protected Component newTopbarTitle(String componentId) {
		return new Label(componentId, _T("Alert Settings"));
	}

}

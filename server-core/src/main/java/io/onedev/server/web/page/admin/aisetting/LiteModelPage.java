package io.cheeta.server.web.page.admin.aisetting;

import static io.cheeta.server.web.translation.Translation._T;

import javax.inject.Inject;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import io.cheeta.server.data.migration.VersionedXmlDoc;
import io.cheeta.server.model.support.administration.AISetting;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.web.editable.PropertyContext;
import io.cheeta.server.web.page.admin.AdministrationPage;

public class LiteModelPage extends AdministrationPage {

	@Inject
	private SettingService settingService;

	public LiteModelPage(PageParameters params) {
		super(params);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		AISetting aiSetting = settingService.getAISetting();
		var oldAuditContent = VersionedXmlDoc.fromBean(aiSetting).toXML();

		Form<?> form = new Form<Void>("form") {

			@Override
			protected void onSubmit() {
				super.onSubmit();
				var newAuditContent = VersionedXmlDoc.fromBean(aiSetting).toXML();
				settingService.saveAISetting(aiSetting);
				auditService.audit(null, "changed AI settings", oldAuditContent, newAuditContent);				
				getSession().success(_T("Lite AI model settings have been saved"));
				
				setResponsePage(LiteModelPage.class);
			}
			
		};
		form.add(PropertyContext.edit("editor", aiSetting, AISetting.PROP_LITE_MODEL_SETTING));
		
		add(form);
	}

	@Override
	protected Component newTopbarTitle(String componentId) {
		return new Label(componentId, _T("Lite AI Model"));
	}

}

package io.cheeta.server.web.component.user.passwordedit;

import static io.cheeta.server.web.translation.Translation._T;

import java.util.HashSet;
import java.util.Set;

import org.apache.shiro.authc.credential.PasswordService;
import org.apache.wicket.Session;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;

import io.cheeta.commons.loader.AppLoader;
import io.cheeta.server.Cheeta;
import io.cheeta.server.service.AuditService;
import io.cheeta.server.service.UserService;
import io.cheeta.server.model.User;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.web.editable.BeanContext;
import io.cheeta.server.web.page.user.UserPage;

public class PasswordEditPanel extends GenericPanel<User> {
	
	public PasswordEditPanel(String id, IModel<User> model) {
		super(id, model);
	}

	private User getUser() {
		return getModelObject();
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		PasswordEditBean bean = new PasswordEditBean();
		
		Set<String> excludedProperties = new HashSet<>();
		
		// in case administrator changes password we do not ask for old password
		if (SecurityUtils.isAdministrator()) 
			excludedProperties.add("oldPassword");
		
		Form<?> form = new Form<Void>("form") {

			@Override
			protected void onSubmit() {
				super.onSubmit();

				var auditService = Cheeta.getInstance(AuditService.class);
				if (getUser().getPassword() != null) {
					if (getPage() instanceof UserPage)
						auditService.audit(null, "changed password in account \"" + getUser().getName() + "\"", null, null);
					Session.get().success(_T("Password has been changed"));
				} else {
					if (getPage() instanceof UserPage)
						auditService.audit(null, "created password in account \"" + getUser().getName() + "\"", null, null);
					Session.get().success(_T("Password has been set"));
				}
					
				getUser().setPassword(AppLoader.getInstance(PasswordService.class).encryptPassword(bean.getNewPassword()));
				Cheeta.getInstance(UserService.class).update(getUser(), null);

				bean.setOldPassword(null);
				
				setResponsePage(getPage().getClass(), getPage().getPageParameters());
			}

		};
		add(form);
		
		form.add(BeanContext.edit("editor", bean, excludedProperties, true));
		form.add(new Button("submit").add(AttributeAppender.append("value", new AbstractReadOnlyModel<>() {
			@Override
			public String getObject() {
				return getUser().getPassword()!=null?_T("Change"):_T("Set");
			}
		})));
	}
	
}

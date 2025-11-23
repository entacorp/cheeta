package io.cheeta.server.web.page.admin.usermanagement;

import static io.cheeta.server.web.translation.Translation._T;

import org.apache.shiro.authc.credential.PasswordService;
import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import com.google.common.collect.Sets;

import io.cheeta.commons.loader.AppLoader;
import io.cheeta.server.Cheeta;
import io.cheeta.server.data.migration.VersionedXmlDoc;
import io.cheeta.server.service.EmailAddressService;
import io.cheeta.server.service.MembershipService;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.service.UserService;
import io.cheeta.server.model.EmailAddress;
import io.cheeta.server.model.Group;
import io.cheeta.server.model.Membership;
import io.cheeta.server.model.User;
import io.cheeta.server.persistence.TransactionService;
import io.cheeta.server.util.Path;
import io.cheeta.server.util.PathNode;
import io.cheeta.server.web.editable.BeanContext;
import io.cheeta.server.web.page.admin.AdministrationPage;
import io.cheeta.server.web.page.user.UserCssResourceReference;
import io.cheeta.server.web.page.user.basicsetting.UserBasicSettingPage;
import io.cheeta.server.web.util.editbean.NewUserBean;

public class NewUserPage extends AdministrationPage {

	private NewUserBean bean = new NewUserBean();
	
	private boolean continueToAdd;
	
	public NewUserPage(PageParameters params) {
		super(params);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		var editor = BeanContext.edit("editor", bean, Sets.newHashSet(User.PROP_NOTIFY_OWN_EVENTS), true);
		
		Form<?> form = new Form<Void>("form") {

			@Override
			protected void onSubmit() {
				super.onSubmit();
				
				User userWithSameName = getUserService().findByName(bean.getName());
				if (userWithSameName != null) {
					editor.error(new Path(new PathNode.Named(User.PROP_NAME)),
							_T("User name already used by another account"));
				} 
				
				if (!bean.isServiceAccount() && getEmailAddressService().findByValue(bean.getEmailAddress()) != null) {
					editor.error(new Path(new PathNode.Named(NewUserBean.PROP_EMAIL_ADDRESS)),
							_T("Email address already used by another user"));
				} 
				if (editor.isValid()) {
					User user = new User();
					user.setName(bean.getName());
					user.setFullName(bean.getFullName());
					user.setServiceAccount(bean.isServiceAccount());
					var defaultLoginGroup = getSettingService().getSecuritySetting().getDefaultGroup();
					if (user.isServiceAccount()) {
						getTransactionService().run(new Runnable() {
							@Override
							public void run() {
								getUserService().create(user);
								if (defaultLoginGroup != null) 
									createMembership(user, defaultLoginGroup);
							}
						});
					} else {
						user.setPassword(AppLoader.getInstance(PasswordService.class).encryptPassword(bean.getPassword()));
						EmailAddress emailAddress = new EmailAddress();
						emailAddress.setValue(bean.getEmailAddress());
						emailAddress.setOwner(user);
						emailAddress.setVerificationCode(null);
						
						getTransactionService().run(new Runnable() {
	
							@Override
							public void run() {
								getUserService().create(user);
								getEmailAddressService().create(emailAddress);
								if (defaultLoginGroup != null) 
									createMembership(user, defaultLoginGroup);
								var newAuditContent = VersionedXmlDoc.fromBean(user).toXML();
								auditService.audit(null, "created account \"" + user.getName() + "\"", null, newAuditContent);
							}
							
						});
					}
										
					Session.get().success(_T("New user created"));
					if (continueToAdd) {
						bean = new NewUserBean();
						replace(BeanContext.edit("editor", bean));
					} else {
						setResponsePage(UserBasicSettingPage.class, UserBasicSettingPage.paramsOf(user));
					}
				}
			}
			
		};
		form.add(editor);
		form.add(new CheckBox("continueToAdd", new IModel<Boolean>() {

			@Override
			public void detach() {
			}

			@Override
			public Boolean getObject() {
				return continueToAdd;
			}

			@Override
			public void setObject(Boolean object) {
				continueToAdd = object;
			}
			
		}));
		add(form);
	}

	private void createMembership(User user, Group group) {
		var membership = new Membership();
		membership.setUser(user);
		membership.setGroup(group);
		user.getMemberships().add(membership);
		getMembershipService().create(membership);
	}

	private SettingService getSettingService() {
		return Cheeta.getInstance(SettingService.class);
	}

	private TransactionService getTransactionService() {
		return Cheeta.getInstance(TransactionService.class);
	}

	private MembershipService getMembershipService() {
		return Cheeta.getInstance(MembershipService.class);
	}

	private UserService getUserService() {
		return Cheeta.getInstance(UserService.class);
	}
	
	private EmailAddressService getEmailAddressService() {
		return Cheeta.getInstance(EmailAddressService.class);
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new UserCssResourceReference()));
	}

	@Override
	protected Component newTopbarTitle(String componentId) {
		return new Label(componentId, _T("Create User"));
	}

}

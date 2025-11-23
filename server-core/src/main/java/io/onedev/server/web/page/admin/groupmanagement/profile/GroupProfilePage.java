package io.cheeta.server.web.page.admin.groupmanagement.profile;

import static io.cheeta.server.web.translation.Translation._T;

import java.io.Serializable;
import java.text.MessageFormat;

import org.apache.wicket.Session;
import org.apache.wicket.feedback.FencedFeedbackPanel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.flow.RedirectToUrlException;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import io.cheeta.server.Cheeta;
import io.cheeta.server.data.migration.VersionedXmlDoc;
import io.cheeta.server.service.AuditService;
import io.cheeta.server.service.GroupService;
import io.cheeta.server.model.Group;
import io.cheeta.server.util.Path;
import io.cheeta.server.util.PathNode;
import io.cheeta.server.web.WebSession;
import io.cheeta.server.web.editable.BeanContext;
import io.cheeta.server.web.editable.BeanEditor;
import io.cheeta.server.web.page.admin.groupmanagement.GroupListPage;
import io.cheeta.server.web.page.admin.groupmanagement.GroupPage;
import io.cheeta.server.web.util.ConfirmClickModifier;

public class GroupProfilePage extends GroupPage {

	private BeanEditor editor;
		
	private String oldName;
	
	public GroupProfilePage(PageParameters params) {
		super(params);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		var oldAuditContent = VersionedXmlDoc.fromBean(getGroup()).toXML();
		editor = BeanContext.editModel("editor", new IModel<Serializable>() {

			@Override
			public void detach() {
			}

			@Override
			public Serializable getObject() {
				return getGroup();
			}

			@Override
			public void setObject(Serializable object) {
				// check contract of GroupManager.save on why we assign oldName here
				oldName = getGroup().getName();
				editor.getDescriptor().copyProperties(object, getGroup());
			}
			
		});

		Form<?> form = new Form<Void>("form") {

			@Override
			protected void onSubmit() {
				super.onSubmit();
				
				Group group = getGroup();
				GroupService groupService = Cheeta.getInstance(GroupService.class);
				Group groupWithSameName = groupService.find(group.getName());
				if (groupWithSameName != null && !groupWithSameName.equals(group)) {
					editor.error(new Path(new PathNode.Named("name")),
							_T("This name has already been used by another group"));
				} 
				if (editor.isValid()) {
					var newAuditContent = VersionedXmlDoc.fromBean(group).toXML();
					groupService.update(group, oldName);
					Cheeta.getInstance(AuditService.class).audit(null, "changed basic settings of group \"" + group.getName() + "\"", oldAuditContent, newAuditContent);
					setResponsePage(GroupProfilePage.class, GroupProfilePage.paramsOf(group));
					Session.get().success(_T("Basic settings updated"));
				}
			}
			
		};	
		form.add(editor);
		form.add(new FencedFeedbackPanel("feedback", form));

		form.add(new Link<Void>("delete") {

			@Override
			public void onClick() {
				var oldAuditContent = VersionedXmlDoc.fromBean(getGroup()).toXML();
				Cheeta.getInstance(GroupService.class).delete(getGroup());
				Cheeta.getInstance(AuditService.class).audit(null, "deleted group \"" + getGroup().getName() + "\"", oldAuditContent, null);

				Session.get().success(MessageFormat.format(_T("Group \"{0}\" deleted"), getGroup().getName()));
				
				String redirectUrlAfterDelete = WebSession.get().getRedirectUrlAfterDelete(Group.class);
				if (redirectUrlAfterDelete != null)
					throw new RedirectToUrlException(redirectUrlAfterDelete);
				else
					setResponsePage(GroupListPage.class);
			}
			
		}.add(new ConfirmClickModifier(MessageFormat.format(_T("Do you really want to delete group \"{0}\"?"), getGroup().getName()))));
		
		add(form);
	}

}

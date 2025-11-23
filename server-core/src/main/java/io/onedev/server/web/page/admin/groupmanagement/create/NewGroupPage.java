package io.cheeta.server.web.page.admin.groupmanagement.create;

import static io.cheeta.server.web.translation.Translation._T;

import org.apache.wicket.Component;
import org.apache.wicket.Session;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import io.cheeta.server.Cheeta;
import io.cheeta.server.data.migration.VersionedXmlDoc;
import io.cheeta.server.service.AuditService;
import io.cheeta.server.service.GroupService;
import io.cheeta.server.model.Group;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.util.Path;
import io.cheeta.server.util.PathNode;
import io.cheeta.server.web.editable.BeanContext;
import io.cheeta.server.web.editable.BeanEditor;
import io.cheeta.server.web.page.admin.AdministrationPage;
import io.cheeta.server.web.page.admin.groupmanagement.GroupCssResourceReference;
import io.cheeta.server.web.page.admin.groupmanagement.GroupListPage;
import io.cheeta.server.web.page.admin.groupmanagement.membership.GroupMembershipsPage;

public class NewGroupPage extends AdministrationPage {

	private Group group = new Group();
	
	public NewGroupPage(PageParameters params) {
		super(params);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		BeanEditor editor = BeanContext.edit("editor", group);
		
		Form<?> form = new Form<Void>("form") {

			@Override
			protected void onSubmit() {
				super.onSubmit();
				
				GroupService groupService = Cheeta.getInstance(GroupService.class);
				Group groupWithSameName = groupService.find(group.getName());
				if (groupWithSameName != null) {
					editor.error(new Path(new PathNode.Named("name")),
							_T("This name has already been used by another group"));
				} 
				if (editor.isValid()) {
					groupService.create(group);
					var newAuditContent = VersionedXmlDoc.fromBean(group).toXML();
					Cheeta.getInstance(AuditService.class).audit(null, "created group \"" + group.getName() + "\"", null, newAuditContent);
					Session.get().success(_T("Group created"));
					setResponsePage(GroupMembershipsPage.class, GroupMembershipsPage.paramsOf(group));
				}
			}
			
		};
		form.add(editor);
		add(form);
	}

	@Override
	protected boolean isPermitted() {
		return SecurityUtils.isAdministrator();
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new GroupCssResourceReference()));
	}

	@Override
	protected Component newTopbarTitle(String componentId) {
		Fragment fragment = new Fragment(componentId, "topbarTitleFrag", this);
		fragment.add(new BookmarkablePageLink<Void>("groups", GroupListPage.class));
		return fragment;
	}

}

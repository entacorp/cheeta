package io.cheeta.server.web.component.user.ident;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.ComponentTag;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.GenericPanel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.request.cycle.RequestCycle;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.UserService;
import io.cheeta.server.model.User;
import io.cheeta.server.web.behavior.dropdown.DropdownHoverBehavior;
import io.cheeta.server.web.component.floating.AlignPlacement;
import io.cheeta.server.web.component.user.UserAvatar;
import io.cheeta.server.web.component.user.card.UserCardPanel;
import io.cheeta.server.web.page.user.profile.UserProfilePage;

public class UserIdentPanel extends GenericPanel<User> {

	private final Mode mode;
	
	public UserIdentPanel(String id, User user, Mode mode) {
		super(id);
		this.mode = mode;
		
		Long userId = user.getId();
		setModel(new LoadableDetachableModel<User>() {

			@Override
			protected User load() {
				return Cheeta.getInstance(UserService.class).load(userId);
			}
			
		});
	}
	
	private User getUser() {
		return getModelObject();
	}
	
	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		add(new UserAvatar("avatar", getUser()).setVisible(mode != Mode.NAME));
		add(new Label("name", getUser().getDisplayName()).setVisible(mode != Mode.AVATAR));
		
		add(AttributeAppender.append("class", "user"));
		
		add(new DropdownHoverBehavior(AlignPlacement.top(8), 350) {

			@Override
			protected Component newContent(String id) {
				return new UserCardPanel(id, getUser());
			}
			
		});
	}

	@Override
	protected void onComponentTag(ComponentTag tag) {
		super.onComponentTag(tag);
		if (getUser().getId() > 0) {
			tag.setName("a");
			tag.put("onclick", "event.stopPropagation()");
			var url = RequestCycle.get().urlFor(UserProfilePage.class, UserProfilePage.paramsOf(getUser()));
			tag.put("href", url.toString());
		} else {
			tag.setName("span");
		}
	}

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssHeaderItem.forReference(new IdentCssResourceReference()));
	}

}

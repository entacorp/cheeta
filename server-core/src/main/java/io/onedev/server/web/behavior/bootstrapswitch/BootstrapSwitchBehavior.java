package io.cheeta.server.web.behavior.bootstrapswitch;

import org.apache.wicket.Component;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;

public class BootstrapSwitchBehavior extends Behavior {

	@Override
	public void renderHead(Component component, IHeaderResponse response) {
		super.renderHead(component, response);
		response.render(JavaScriptHeaderItem.forReference(new BootstrapSwitchResourceReference()));
		
		String script = String.format("cheeta.server.bootstrapSwitch.onDomReady('%s');", component.getMarkupId(true));
		response.render(OnDomReadyHeaderItem.forScript(script));
	}

}

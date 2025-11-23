package io.cheeta.server.plugin.notification.discord;

import io.cheeta.server.model.support.channelnotification.ChannelNotificationSetting;
import io.cheeta.server.annotation.Editable;

@Editable(name="Discord Notifications", group="Notification", order=200, description="Set up discord notification " +
		"settings. Settings will be inherited by child projects, and can be overridden by defining settings with " +
		"same webhook url")
public class DiscordNotificationSetting extends ChannelNotificationSetting {

	private static final long serialVersionUID = 1L;

}

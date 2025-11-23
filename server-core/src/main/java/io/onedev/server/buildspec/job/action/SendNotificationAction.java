package io.cheeta.server.buildspec.job.action;

import javax.validation.constraints.NotEmpty;

import io.cheeta.server.Cheeta;
import io.cheeta.server.event.project.build.BuildFinished;
import io.cheeta.server.model.Build;
import io.cheeta.server.notification.BuildNotificationManager;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.NotificationReceiver;

@Editable(name="Send notification", order=200)
public class SendNotificationAction extends PostBuildAction {

	private static final long serialVersionUID = 1L;
	
	private String receivers;
	
	@Editable(order=1000)
	@NotificationReceiver
	@NotEmpty
	public String getReceivers() {
		return receivers;
	}

	public void setReceivers(String receivers) {
		this.receivers = receivers;
	}

	@Override
	public void execute(Build build) {
		io.cheeta.server.buildspec.job.action.notificationreceiver.NotificationReceiver parsedReceiver = 
				io.cheeta.server.buildspec.job.action.notificationreceiver.NotificationReceiver.parse(getReceivers(), build);
		Cheeta.getInstance(BuildNotificationManager.class).notify(new BuildFinished(build), parsedReceiver.getEmails());
	}

	@Override
	public String getDescription() {
		return "Send notification to " + receivers;
	}

}

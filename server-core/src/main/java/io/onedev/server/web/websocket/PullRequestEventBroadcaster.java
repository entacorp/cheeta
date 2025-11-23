package io.cheeta.server.web.websocket;

import io.cheeta.server.event.Listen;
import io.cheeta.server.event.project.pullrequest.PullRequestEvent;
import io.cheeta.server.model.PullRequest;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class PullRequestEventBroadcaster {
	
	private final WebSocketService webSocketService;
	
	@Inject
	public PullRequestEventBroadcaster(WebSocketService webSocketService) {
		this.webSocketService = webSocketService;
	}

	@Listen
	public void on(PullRequestEvent event) {
		webSocketService.notifyObservableChange(PullRequest.getChangeObservable(event.getRequest().getId()), event.getSourcePage());
	}

}
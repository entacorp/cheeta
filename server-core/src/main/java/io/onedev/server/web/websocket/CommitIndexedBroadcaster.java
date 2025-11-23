package io.cheeta.server.web.websocket;

import javax.inject.Inject;
import javax.inject.Singleton;

import io.cheeta.server.event.Listen;
import io.cheeta.server.event.project.CommitIndexed;

@Singleton
public class CommitIndexedBroadcaster {

	private final WebSocketService webSocketService;
	
	@Inject
	public CommitIndexedBroadcaster(WebSocketService webSocketService) {
		this.webSocketService = webSocketService;
	}
	
	@Listen
	public void on(CommitIndexed event) {
		webSocketService.notifyObservableChange(CommitIndexed.getChangeObservable(event.getCommitId().name()), event.getSourcePage());
	}

}
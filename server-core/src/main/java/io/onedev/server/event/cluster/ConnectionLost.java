package io.cheeta.server.event.cluster;

public class ConnectionLost extends ConnectionEvent {
	
	public ConnectionLost(String server) {
		super(server);
	}

}

package io.cheeta.server.terminal;

public interface Terminal {
	
	String getSessionId();
	
	void sendOutput(String output);
	
	void sendError(String error);
	
	void close();

}

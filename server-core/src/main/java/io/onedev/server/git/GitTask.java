package io.cheeta.server.git;

import io.cheeta.commons.utils.command.Commandline;

import java.io.IOException;

public interface GitTask<T> {

	T call(Commandline git) throws IOException;
	
}

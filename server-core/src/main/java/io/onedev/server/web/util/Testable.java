package io.cheeta.server.web.util;

import java.io.Serializable;

import io.cheeta.commons.utils.TaskLogger;

public interface Testable<T extends Serializable> {
	
	void test(T data, TaskLogger logger);
	
}

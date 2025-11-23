package io.cheeta.server.data.migration;

public interface MigrationListener {
	
	void afterMigration(Object bean);
	
}

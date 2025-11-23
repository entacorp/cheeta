package io.cheeta.server.search.code;

import org.jspecify.annotations.Nullable;

import org.eclipse.jgit.lib.ObjectId;

import io.cheeta.commons.jsymbol.Symbol;
import io.cheeta.commons.jsymbol.SymbolExtractor;

public interface CodeIndexService {
	
	void indexAsync(Long projectId, ObjectId commitId);
	
	boolean isIndexed(Long projectId, ObjectId commitId);
	
	String getIndexVersion(@Nullable SymbolExtractor<Symbol> extractor);
	
}

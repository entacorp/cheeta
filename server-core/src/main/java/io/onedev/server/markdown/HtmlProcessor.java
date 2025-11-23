package io.cheeta.server.markdown;

import org.jspecify.annotations.Nullable;

import org.jsoup.nodes.Document;

import io.cheeta.commons.loader.ExtensionPoint;
import io.cheeta.server.model.Project;
import io.cheeta.server.web.component.markdown.SuggestionSupport;
import io.cheeta.server.web.page.project.blob.render.BlobRenderContext;

@ExtensionPoint
public interface HtmlProcessor {
	
	void process(Document document, @Nullable Project project,
				 @Nullable BlobRenderContext blobRenderContext,
				 @Nullable SuggestionSupport suggestionSupport,
				 boolean forExternal);
	
}

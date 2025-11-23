package io.cheeta.server.web.page.project.blob;

import org.apache.wicket.core.request.handler.IPartialPageRequestHandler;
import org.eclipse.jgit.lib.ObjectId;

import io.cheeta.server.web.util.AjaxPayload;

public class RevisionResolved extends AjaxPayload {

	private final ObjectId resolvedRevision;
	
	public RevisionResolved(IPartialPageRequestHandler partialPageRequestHandler, ObjectId resolvedRevision) {
		super(partialPageRequestHandler);
		this.resolvedRevision = resolvedRevision;
	}

	public ObjectId getResolvedRevision() {
		return resolvedRevision;
	}

}

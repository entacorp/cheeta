package io.cheeta.server.attachment;

import io.cheeta.server.model.Project;

public interface AttachmentStorageSupport {
	
	Project getAttachmentProject();
	
	String getAttachmentGroup();
	
}

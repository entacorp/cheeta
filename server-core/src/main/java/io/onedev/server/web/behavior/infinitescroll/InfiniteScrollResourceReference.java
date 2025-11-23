package io.cheeta.server.web.behavior.infinitescroll;

import io.cheeta.server.web.page.base.BaseDependentResourceReference;

public class InfiniteScrollResourceReference extends BaseDependentResourceReference {

	private static final long serialVersionUID = 1L;
	
	public InfiniteScrollResourceReference() {
		super(InfiniteScrollResourceReference.class, "infinite-scroll.js");
	}

}

package io.cheeta.server.util;

import io.cheeta.commons.utils.ExplicitException;
import io.cheeta.server.Cheeta;
import io.cheeta.server.service.LinkSpecService;
import io.cheeta.server.model.LinkSpec;

public class LinkDescriptor {
	
	private final LinkSpec spec;
	
	private final boolean opposite;
	
	public LinkDescriptor(LinkSpec spec, boolean opposite) {
		this.spec = spec;
		this.opposite = opposite;
	}
	
	public LinkDescriptor(String linkName) {
		spec = Cheeta.getInstance(LinkSpecService.class).find(linkName);
		if (spec == null)
			throw new ExplicitException("Link spec not found: " + linkName);
		opposite = !linkName.equals(spec.getName());
	}
	
	public LinkSpec getSpec() {
		return spec;
	}

	public boolean isOpposite() {
		return opposite;
	}
	
	public String getName() {
		return spec.getName(opposite);
	}
	
}
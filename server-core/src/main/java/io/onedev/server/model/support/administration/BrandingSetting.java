package io.cheeta.server.model.support.administration;

import java.io.Serializable;

public class BrandingSetting implements Serializable {

	private static final long serialVersionUID = 1L;
			
	private String name = "Cheeta";
		
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
		
}

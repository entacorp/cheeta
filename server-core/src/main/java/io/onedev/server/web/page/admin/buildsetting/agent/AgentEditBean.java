package io.cheeta.server.web.page.admin.buildsetting.agent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;

import io.cheeta.server.model.AgentAttribute;
import io.cheeta.server.validation.Validatable;
import io.cheeta.server.annotation.ClassValidating;
import io.cheeta.server.annotation.Editable;

@Editable
@ClassValidating
public class AgentEditBean implements Serializable, Validatable {

	private static final long serialVersionUID = 1L;

	private List<AgentAttribute> attributes = new ArrayList<>();

	@Editable
	public List<AgentAttribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<AgentAttribute> attributes) {
		this.attributes = attributes;
	}

	@Override
	public boolean isValid(ConstraintValidatorContext context) {
		Set<String> attributeNames = new HashSet<>();
		for (AgentAttribute attribute: attributes) {
			if (!attributeNames.add(attribute.getName())) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate("Duplicate attribute (" + attribute.getName() + ")")
						.addPropertyNode("attributes").addConstraintViolation();
				return false;
			} 
		}
		return true;
	}
	
}

package io.cheeta.server.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.cheeta.commons.utils.StringUtils;
import io.cheeta.server.annotation.ProjectQuery;

public class ProjectQueryValidator implements ConstraintValidator<ProjectQuery, String> {

	private String message;
	
	@Override
	public void initialize(ProjectQuery constaintAnnotation) {
		message = constaintAnnotation.message();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext constraintContext) {
		if (value == null) {
			return true;
		} else {
			try {
				io.cheeta.server.search.entity.project.ProjectQuery.parse(value);
				return true;
			} catch (Exception e) {
				constraintContext.disableDefaultConstraintViolation();
				String message = this.message;
				if (message.length() == 0) {
					if (StringUtils.isNotBlank(e.getMessage()))
						message = e.getMessage();
					else
						message = "Malformed query";
				}
				
				constraintContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
				return false;
			}
		}
	}
	
}

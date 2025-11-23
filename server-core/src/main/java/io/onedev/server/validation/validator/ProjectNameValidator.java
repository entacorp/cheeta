package io.cheeta.server.validation.validator;

import java.util.regex.Pattern;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.ProjectService;
import io.cheeta.server.annotation.ProjectName;

public class ProjectNameValidator implements ConstraintValidator<ProjectName, String> {

	private static final Pattern PATTERN = Pattern.compile("\\w[\\w-.]*");
	
	private String message;
	
	@Override
	public void initialize(ProjectName constaintAnnotation) {
		message = constaintAnnotation.message();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext constraintContext) {
		if (value == null) 
			return true;
		
		if (!PATTERN.matcher(value).matches()) {
			constraintContext.disableDefaultConstraintViolation();
			String message = this.message;
			if (message.length() == 0) {
				message = "Should start with alphanumeric or underscore, and contains only "
						+ "alphanumeric, underscore, dash, or dot";
			}
			constraintContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
			return false;
		} else if (Cheeta.getInstance(ProjectService.class).getReservedNames().contains(value)) {
			constraintContext.disableDefaultConstraintViolation();
			String message = this.message;
			if (message.length() == 0)
				message = "'" + value + "' is a reserved name";
			constraintContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
			return false;
		} else {
			return true;
		}	
	}
	
}

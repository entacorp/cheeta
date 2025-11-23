package io.cheeta.server.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.cheeta.server.annotation.ClassValidating;
import io.cheeta.server.validation.Validatable;

public class ClassValidatingValidator implements ConstraintValidator<ClassValidating, Validatable> {

	@Override
	public void initialize(ClassValidating constraintAnnotation) {
	}

	@Override
	public boolean isValid(Validatable value, ConstraintValidatorContext constraintValidatorContext) {
		return value.isValid(constraintValidatorContext);
	}
	
}
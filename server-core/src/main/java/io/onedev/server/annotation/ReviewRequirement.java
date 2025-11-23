package io.cheeta.server.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

import io.cheeta.server.validation.validator.ReviewRequirementValidator;

@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=ReviewRequirementValidator.class) 
public @interface ReviewRequirement {

	String message() default ""; 
	
	Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
    
}

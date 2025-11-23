package io.cheeta.server.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.cheeta.commons.utils.StringUtils;
import io.cheeta.server.model.Project;
import io.cheeta.server.annotation.PullRequestQuery;

public class PullRequestQueryValidator implements ConstraintValidator<PullRequestQuery, String> {

	private String message;
	
	@Override
	public void initialize(PullRequestQuery constaintAnnotation) {
		message = constaintAnnotation.message();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext constraintContext) {
		if (value == null) {
			return true;
		} else {
			Project project = Project.get();
			try {
				io.cheeta.server.search.entity.pullrequest.PullRequestQuery.parse(project, value, true);
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

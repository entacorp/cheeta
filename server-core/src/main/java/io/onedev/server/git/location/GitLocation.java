package io.cheeta.server.git.location;

import java.io.Serializable;

import javax.validation.ConstraintValidatorContext;

import io.cheeta.server.git.CommandUtils;
import io.cheeta.server.validation.Validatable;
import io.cheeta.server.annotation.ClassValidating;
import io.cheeta.server.annotation.Editable;

/**
 * Git relevant settings.
 * 
 * @author robin
 *
 */
@Editable
@ClassValidating
public abstract class GitLocation implements Serializable, Validatable {

	private static final long serialVersionUID = 1L;
	
	/**
	 * Get git executable, for instance <tt>/usr/bin/git</tt>.
	 * 
	 * @return
	 * 			git executable
	 */
	public abstract String getExecutable();

	@Override
	public boolean isValid(ConstraintValidatorContext context) {
		if (getExecutable() != null) {
			String error = CommandUtils.checkError(getExecutable());
			if (error != null) {
				context.disableDefaultConstraintViolation();
				context.buildConstraintViolationWithTemplate(error).addConstraintViolation();
				return false;
			} else {
				return true;
			}
		} else {
			return true;
		}
	}
	
}

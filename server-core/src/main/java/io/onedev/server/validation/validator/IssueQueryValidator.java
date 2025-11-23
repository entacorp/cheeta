package io.cheeta.server.validation.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import io.cheeta.commons.utils.StringUtils;
import io.cheeta.server.model.Project;
import io.cheeta.server.search.entity.issue.IssueQueryParseOption;
import io.cheeta.server.annotation.IssueQuery;

public class IssueQueryValidator implements ConstraintValidator<IssueQuery, String> {

	private String message;
	
	private boolean withCurrentProjectCriteria;
	
	private boolean withCurrentUserCriteria;
	
	private boolean withCurrentBuildCriteria;
	
	private boolean withCurrentPullRequestCriteria;
	
	private boolean withCurrentCommitCriteria;
	
	private boolean withCurrentIssueCriteria;
	
	@Override
	public void initialize(IssueQuery constaintAnnotation) {
		message = constaintAnnotation.message();
		withCurrentProjectCriteria = constaintAnnotation.withCurrentProjectCriteria();
		withCurrentUserCriteria = constaintAnnotation.withCurrentUserCriteria();
		withCurrentBuildCriteria = constaintAnnotation.withCurrentBuildCriteria();
		withCurrentPullRequestCriteria = constaintAnnotation.withCurrentPullRequestCriteria();
		withCurrentCommitCriteria = constaintAnnotation.withCurrentCommitCriteria();
		withCurrentIssueCriteria = constaintAnnotation.withCurrentIssueCriteria();
	}

	@Override
	public boolean isValid(String value, ConstraintValidatorContext constraintContext) {
		if (value == null) {
			return true;
		} else {
			Project project = Project.get();
			try {
				IssueQueryParseOption option = new IssueQueryParseOption()
						.withCurrentProjectCriteria(withCurrentProjectCriteria)
						.withCurrentUserCriteria(withCurrentUserCriteria)
						.withCurrentBuildCriteria(withCurrentBuildCriteria)
						.withCurrentCommitCriteria(withCurrentCommitCriteria)
						.withCurrentPullRequestCriteria(withCurrentPullRequestCriteria)
						.withCurrentIssueCriteria(withCurrentIssueCriteria);
				io.cheeta.server.search.entity.issue.IssueQuery.parse(project, value, option, true);
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

package io.cheeta.server.buildspec.job.gitcredential;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.ConstraintValidatorContext;

import javax.validation.constraints.NotEmpty;

import io.cheeta.k8shelper.CloneInfo;
import io.cheeta.k8shelper.HttpCloneInfo;
import io.cheeta.server.Cheeta;
import io.cheeta.server.web.UrlService;
import io.cheeta.server.model.Build;
import io.cheeta.server.model.Project;
import io.cheeta.server.validation.Validatable;
import io.cheeta.server.annotation.ClassValidating;
import io.cheeta.server.annotation.ChoiceProvider;
import io.cheeta.server.annotation.Editable;

@Editable(name="HTTP(S)", order=200)
@ClassValidating
public class HttpCredential implements GitCredential, Validatable {

	private static final long serialVersionUID = 1L;

	private String accessTokenSecret;

	@Editable(order=200, description="Specify a <a href='https://docs.cheeta.io/tutorials/cicd/job-secrets' target='_blank'>job secret</a> to be used as access token")
	@ChoiceProvider("getAccessTokenSecretChoices")
	@NotEmpty
	public String getAccessTokenSecret() {
		return accessTokenSecret;
	}

	public void setAccessTokenSecret(String accessTokenSecret) {
		this.accessTokenSecret = accessTokenSecret;
	}
	
	@SuppressWarnings("unused")
	private static List<String> getAccessTokenSecretChoices() {
		return Project.get().getHierarchyJobSecrets()
				.stream().map(it->it.getName()).collect(Collectors.toList());
	}

	@Override
	public CloneInfo newCloneInfo(Build build, String jobToken) {
		return new HttpCloneInfo(Cheeta.getInstance(UrlService.class).cloneUrlFor(build.getProject(), false),
				build.getJobAuthorizationContext().getSecretValue(accessTokenSecret));
	}

	@Override
	public boolean isValid(ConstraintValidatorContext context) {
		if (!Project.get().getHierarchyJobSecrets().stream()
				.anyMatch(it->it.getName().equals(accessTokenSecret))) {
			context.disableDefaultConstraintViolation();
			context.buildConstraintViolationWithTemplate("Secret not found (" + accessTokenSecret + ")")
					.addPropertyNode("accessTokenSecret")
					.addConstraintViolation();
			return false;
		} else {
			return true;
		}
	}

}

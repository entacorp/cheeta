package io.cheeta.server.buildspec.step;

import io.cheeta.commons.codeassist.InputSuggestion;
import io.cheeta.commons.utils.ExplicitException;
import io.cheeta.commons.utils.TaskLogger;
import io.cheeta.k8shelper.ServerStepResult;
import io.cheeta.server.Cheeta;
import io.cheeta.server.annotation.*;
import io.cheeta.server.buildspec.BuildSpec;
import io.cheeta.server.service.BuildService;
import io.cheeta.server.service.ProjectService;
import io.cheeta.server.service.UserService;
import io.cheeta.server.git.GitUtils;
import io.cheeta.server.git.service.GitService;
import io.cheeta.server.git.service.RefFacade;
import io.cheeta.server.model.Project;
import io.cheeta.server.persistence.SessionService;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;

import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Editable(name="Create Tag", order=300)
public class CreateTagStep extends ServerSideStep {

	private static final long serialVersionUID = 1L;
	
	private String tagName;
	
	private String tagMessage;
	
	private String accessTokenSecret;
	
	@Editable(order=1000, description="Specify name of the tag")
	@Interpolative(variableSuggester="suggestVariables")
	@TagName
	@NotEmpty
	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}
	
	@Editable(order=1050, description="Optionally specify message of the tag")
	@Multiline
	@Interpolative(variableSuggester="suggestVariables")
	public String getTagMessage() {
		return tagMessage;
	}

	public void setTagMessage(String tagMessage) {
		this.tagMessage = tagMessage;
	}

	@SuppressWarnings("unused")
	private static List<InputSuggestion> suggestVariables(String matchWith) {
		return BuildSpec.suggestVariables(matchWith, true, true, false);
	}

	@Editable(order=1060, description="For build commit not reachable from default branch, " +
			"a <a href='https://docs.cheeta.io/tutorials/cicd/job-secrets' target='_blank'>job secret</a> should be specified as access token with create tag permission")
	@ChoiceProvider("getAccessTokenSecretChoices")
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
	public ServerStepResult run(Long buildId, File inputDir, TaskLogger logger) {
		return Cheeta.getInstance(SessionService.class).call(() -> {
			var build = Cheeta.getInstance(BuildService.class).load(buildId);
			PersonIdent taggerIdent = Cheeta.getInstance(UserService.class).getSystem().asPerson();
			Project project = build.getProject();
			String tagName = getTagName();

			if (!Repository.isValidRefName(GitUtils.tag2ref(tagName))) {
				logger.error("Invalid tag name: " + tagName);
				return new ServerStepResult(false);
			}

			if (build.canCreateTag(getAccessTokenSecret(), tagName)) {
				RefFacade tagRef = project.getTagRef(tagName);
				if (tagRef != null)
					Cheeta.getInstance(ProjectService.class).deleteTag(project, tagName);
				Cheeta.getInstance(GitService.class).createTag(project, tagName, build.getCommitHash(),
						taggerIdent, getTagMessage(), false);
			} else {
				throw new ExplicitException("This build is not authorized to create tag '" + tagName + "'");
			}
			return new ServerStepResult(true);
		});
	}

}

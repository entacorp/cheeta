package io.cheeta.server.model.support.administration;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import edu.emory.mathcs.backport.java.util.Collections;
import io.cheeta.commons.utils.ExplicitException;
import io.cheeta.commons.utils.match.PathMatcher;
import io.cheeta.server.Cheeta;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.service.ProjectService;
import io.cheeta.server.model.Project;
import io.cheeta.server.util.patternset.PatternSet;
import io.cheeta.server.util.usage.Usage;
import io.cheeta.server.web.component.issue.workflowreconcile.UndefinedFieldResolution;
import io.cheeta.server.web.component.issue.workflowreconcile.UndefinedFieldValue;
import io.cheeta.server.web.component.issue.workflowreconcile.UndefinedFieldValuesResolution;

@Editable
public class ServiceDeskSetting implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private List<IssueCreationSetting> issueCreationSettings = new ArrayList<>();

	@SuppressWarnings("unused")
	private static List<String> getProjectChoices() {
		List<String> projectPaths = Cheeta.getInstance(ProjectService.class)
				.query().stream().map(it->it.getPath()).collect(Collectors.toList());
		Collections.sort(projectPaths);
		return projectPaths;
	}
	
	@Editable(order=300, description="Specify issue creation settings. For a particular sender and project, " +
			"the first matching entry will take effect. Issue creation will be disallowed if no matching " +
			"entry found")
	@Valid
	public List<IssueCreationSetting> getIssueCreationSettings() {
		return issueCreationSettings;
	}

	public void setIssueCreationSettings(List<IssueCreationSetting> issueCreationSettings) {
		this.issueCreationSettings = issueCreationSettings;
	}
	
	public IssueCreationSetting getIssueCreationSetting(Project project) {
		for (IssueCreationSetting setting: issueCreationSettings) {
			String projectPatterns = setting.getApplicableProjects();
			if (projectPatterns == null)
				projectPatterns = "**";
			PatternSet projectPatternSet = PatternSet.parse(projectPatterns);
			
			if (projectPatternSet.matches(new PathMatcher(), project.getPath())) 
				return setting;
		}
		String errorMessage = String.format("No issue creation settings for project: %s", project.getPath());
		throw new ExplicitException(errorMessage);
	}
	
	public void onMoveProject(String oldPath, String newPath) {
		for (IssueCreationSetting setting: getIssueCreationSettings()) {
			setting.setApplicableProjects(Project.substitutePath(
					setting.getApplicableProjects(), oldPath, newPath));
		}
	}
	
	public Usage onDeleteProject(String projectPath) {
		Usage usage = new Usage();
		
		int index = 1;
		for (IssueCreationSetting setting: getIssueCreationSettings()) {
			if (Project.containsPath(setting.getApplicableProjects(), projectPath))
				usage.add("issue creation setting #" + index + ": applicable projects");
			index++;
		}
		return usage.prefix("service desk settings");
	}

	public Set<String> getUndefinedIssueFields() {
		Set<String> undefinedFields = new HashSet<>();
		for (IssueCreationSetting setting: getIssueCreationSettings()) 
			undefinedFields.addAll(setting.getUndefinedFields());
		return undefinedFields;
	}

	public Collection<UndefinedFieldValue> getUndefinedIssueFieldValues() {
		Collection<UndefinedFieldValue> undefinedFieldValues = new HashSet<>(); 
		for (IssueCreationSetting setting: getIssueCreationSettings()) 
			undefinedFieldValues.addAll(setting.getUndefinedFieldValues());
		return undefinedFieldValues;
	}
	
	public void fixUndefinedIssueFields( Map<String, UndefinedFieldResolution> resolutions) {
		for (IssueCreationSetting setting: getIssueCreationSettings()) 
			setting.fixUndefinedFields(resolutions);
	}

	public void fixUndefinedIssueFieldValues(Map<String, UndefinedFieldValuesResolution> resolutions) {
		for (IssueCreationSetting setting: getIssueCreationSettings()) 
			setting.fixUndefinedFieldValues(resolutions);
	}

}

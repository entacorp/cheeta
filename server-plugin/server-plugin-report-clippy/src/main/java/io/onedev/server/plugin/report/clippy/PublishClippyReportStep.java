package io.cheeta.server.plugin.report.clippy;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import io.cheeta.commons.codeassist.InputSuggestion;
import io.cheeta.commons.utils.FileUtils;
import io.cheeta.commons.utils.TaskLogger;
import io.cheeta.server.Cheeta;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.Interpolative;
import io.cheeta.server.annotation.Patterns;
import io.cheeta.server.buildspec.BuildSpec;
import io.cheeta.server.buildspec.step.StepGroup;
import io.cheeta.server.codequality.CodeProblem;
import io.cheeta.server.model.Build;
import io.cheeta.server.plugin.report.problem.PublishProblemReportStep;

import javax.validation.constraints.NotEmpty;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Editable(order=10000, group=StepGroup.PUBLISH, name="Clippy Report")
public class PublishClippyReportStep extends PublishProblemReportStep {

	private static final long serialVersionUID = 1L;
	
	@Editable(order=100, description="Specify <a href='https://github.com/rust-lang/rust-clippy'>rust clippy</a> json output file relative to <a href='https://docs.cheeta.io/concepts#job-workspace'>job workspace</a>. "
			+ "This file can be generated with clippy json output option, for instance <code>cargo clippy --message-format json>check-result.json</code>. Use * or ? for pattern match")
	@Interpolative(variableSuggester="suggestVariables")
	@Patterns(path=true)
	@NotEmpty
	@Override
	public String getFilePatterns() {
		return super.getFilePatterns();
	}

	@Override
	public void setFilePatterns(String filePatterns) {
		super.setFilePatterns(filePatterns);
	}
	
	@SuppressWarnings("unused")
	private static List<InputSuggestion> suggestVariables(String matchWith) {
		return BuildSpec.suggestVariables(matchWith, true, true, false);
	}
	
	@Override
	protected List<CodeProblem> process(Build build, File inputDir, File reportDir, TaskLogger logger) {
		try {
			var mapper = Cheeta.getInstance(ObjectMapper.class);
			List<CodeProblem> problems = new ArrayList<>();
			int baseLen = inputDir.getAbsolutePath().length()+1;
			for (File file: FileUtils.listFiles(inputDir, Lists.newArrayList("**"), Lists.newArrayList())) {
				logger.log("Processing clippy report: " + file.getAbsolutePath().substring(baseLen));
				var problemNodes = new ArrayList<JsonNode>();
				for (var line: FileUtils.readLines(file, StandardCharsets.UTF_8))
					problemNodes.add(mapper.readTree(line));
				problems.addAll(ClippyReportParser.parse(build, problemNodes, logger));
			}
			return problems;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}

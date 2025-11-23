package io.cheeta.server.plugin.report.jest;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

import io.cheeta.commons.codeassist.InputSuggestion;
import io.cheeta.commons.utils.ExceptionUtils;
import io.cheeta.commons.utils.FileUtils;
import io.cheeta.commons.utils.TaskLogger;
import io.cheeta.server.Cheeta;
import io.cheeta.server.buildspec.BuildSpec;
import io.cheeta.server.buildspec.step.StepGroup;
import io.cheeta.server.model.Build;
import io.cheeta.server.plugin.report.unittest.PublishUnitTestReportStep;
import io.cheeta.server.plugin.report.unittest.UnitTestReport;
import io.cheeta.server.plugin.report.unittest.UnitTestReport.TestCase;
import io.cheeta.server.annotation.Editable;
import io.cheeta.server.annotation.Interpolative;
import io.cheeta.server.annotation.Patterns;

@Editable(order=10000, group=StepGroup.PUBLISH, name="Jest Test Report")
public class PublishJestReportStep extends PublishUnitTestReportStep {

	private static final long serialVersionUID = 1L;

	@Editable(order=100, description="Specify Jest test result file in json format relative to <a href='https://docs.cheeta.io/concepts#job-workspace'>job workspace</a>. "
			+ "This file can be generated via Jest option <tt>'--json'</tt> and <tt>'--outputFile'</tt>. Use * or ? for pattern match")
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
	protected UnitTestReport process(Build build, File inputDir, TaskLogger logger) {
		ObjectMapper mapper = Cheeta.getInstance(ObjectMapper.class);

		List<TestCase> testCases = new ArrayList<>();
		int baseLen = inputDir.getAbsolutePath().length()+1;
		for (File file: FileUtils.listFiles(inputDir, Lists.newArrayList("**"), Lists.newArrayList())) {
			logger.log("Processing Jest test report: " + file.getAbsolutePath().substring(baseLen));
			try {
				testCases.addAll(JestReportParser.parse(build, mapper.readTree(file)));
			} catch (Exception e) {
				throw ExceptionUtils.unchecked(e);
			}
		}
		if (!testCases.isEmpty())
			return new UnitTestReport(testCases, false);
		else
			return null;
	}

}

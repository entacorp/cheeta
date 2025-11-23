package io.cheeta.server.plugin.imports.github;

import static io.cheeta.server.web.translation.Translation._T;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.Lists;

import io.cheeta.commons.utils.TaskLogger;
import io.cheeta.server.Cheeta;
import io.cheeta.server.service.ProjectService;
import io.cheeta.server.imports.IssueImporter;
import io.cheeta.server.persistence.TransactionService;
import io.cheeta.server.web.component.taskbutton.TaskResult;
import io.cheeta.server.web.component.taskbutton.TaskResult.HtmlMessgae;
import io.cheeta.server.web.util.ImportStep;

public class GitHubIssueImporter implements IssueImporter {

	private static final long serialVersionUID = 1L;
	
	private final ImportStep<ImportServer> serverStep = new ImportStep<ImportServer>() {

		private static final long serialVersionUID = 1L;

		@Override
		public String getTitle() {
			return _T("Authenticate to GitHub");
		}

		@Override
		protected ImportServer newSetting() {
			return new ImportServer();
		}
		
	};
	
	private final ImportStep<ImportRepository> repositoryStep = new ImportStep<ImportRepository>() {

		private static final long serialVersionUID = 1L;

		@Override
		public String getTitle() {
			return _T("Choose repository");
		}

		@Override
		protected ImportRepository newSetting() {
			ImportRepository repository = new ImportRepository();
			repository.server = serverStep.getSetting();
			return repository;
		}
		
	};
	
	private final ImportStep<IssueImportOption> optionStep = new ImportStep<IssueImportOption>() {

		private static final long serialVersionUID = 1L;

		@Override
		public String getTitle() {
			return _T("Specify import option");
		}

		@Override
		protected IssueImportOption newSetting() {
			return serverStep.getSetting().buildIssueImportOption(
					Lists.newArrayList(repositoryStep.getSetting().getRepository()));
		}
		
	};
	
	@Override
	public String getName() {
		return GitHubModule.NAME;
	}
	
	@Override
	public TaskResult doImport(Long projectId, boolean dryRun, TaskLogger logger) {
		return Cheeta.getInstance(TransactionService.class).call(() -> {
			var project = Cheeta.getInstance(ProjectService.class).load(projectId);
			logger.log("Importing issues from repository " + repositoryStep.getSetting().getRepository() + "...");
			Map<String, Optional<Long>> userIds = new HashMap<>();

			ImportResult result = serverStep.getSetting().importIssues(repositoryStep.getSetting().getRepository(),
					project, optionStep.getSetting(), userIds, dryRun, logger);
			return new TaskResult(true, new HtmlMessgae(result.toHtml("Issues imported successfully")));
		});
	}

	@Override
	public List<ImportStep<? extends Serializable>> getSteps() {
		return Lists.newArrayList(serverStep, repositoryStep, optionStep);
	}

}
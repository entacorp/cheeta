package io.cheeta.server.plugin.pack.nuget;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.web.component.codesnippet.CodeSnippetPanel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.Model;

import static io.cheeta.server.plugin.pack.nuget.NugetPackHandler.HANDLER_ID;
import static io.cheeta.server.web.translation.Translation._T;

public class NugetHelpPanel extends Panel {
	
	private final String projectPath;
	
	public NugetHelpPanel(String id, String projectPath) {
		super(id);
		this.projectPath = projectPath;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		var registryUrl = getServerUrl() + "/" + projectPath + "/~" + HANDLER_ID + "/index.json";
		add(new CodeSnippetPanel("addSource", Model.of("$ dotnet nuget add source --name cheeta --username <cheeta_account_name> --password <cheeta_account_password> --store-password-in-clear-text " + registryUrl)));

		add(new CodeSnippetPanel("pushCommand", Model.of("$ dotnet nuget push -s cheeta /path/to/<PackageId>.<PackageVersion>.nupkg")));
		
		add(new CodeSnippetPanel("jobCommands", new LoadableDetachableModel<String>() {
			@Override
			protected String load() {
				return "" +
						"# " + _T("Use job token to tell Cheeta the build pushing the package") + "\n" +
						"# " + _T("Job secret 'access-token' should be defined in project build setting as an access token with package write permission") + "\n\n" +
						"dotnet nuget add source --name cheeta --username @job_token@ --password @secret:access-token@ --store-password-in-clear-text " + registryUrl;
			}

		}));
	}

	private String getServerUrl() {
		return Cheeta.getInstance(SettingService.class).getSystemSetting().getServerUrl();
	}
	
}

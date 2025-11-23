package io.cheeta.server.plugin.pack.gem;

import static io.cheeta.server.plugin.pack.gem.GemPackHandler.HANDLER_ID;
import static io.cheeta.server.web.translation.Translation._T;

import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.web.component.codesnippet.CodeSnippetPanel;

public class GemHelpPanel extends Panel {
	
	private final String projectPath;
	
	public GemHelpPanel(String id, String projectPath) {
		super(id);
		this.projectPath = projectPath;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		var registryUrl = getServerUrl() + "/" + projectPath + "/~" + HANDLER_ID;
		var addSourceCommands = "" +
				"---\n" +
				registryUrl + ": Bearer <cheeta_access_token>"; 
		add(new CodeSnippetPanel("addSource", Model.of(addSourceCommands)));
		
		var pushCommand = "gem push --host " + registryUrl + " /path/to/<package>-<version>.gem";
		add(new CodeSnippetPanel("pushCommand", Model.of(pushCommand)));

		var jobCommands = "" +
				"mkdir -p $HOME/.gem\n" +
				"\n" +
				"# " + _T("Use job token to tell Cheeta the build publishing the package") + "\n" +
				"# " + _T("Job secret 'access-token' should be defined in project build setting as an access token with package write permission") + "\n\n" +
				"cat << EOF > $HOME/.gem/credentials\n" +
				"---\n" +
				registryUrl + ": Bearer @job_token@:@secret:access-token@\n" +
				"EOF\n" +
				"\n" +
				"chmod 0600 $HOME/.gem/credentials";
		
		add(new CodeSnippetPanel("jobCommands", Model.of(jobCommands)));
	}

	private String getServerUrl() {
		return Cheeta.getInstance(SettingService.class).getSystemSetting().getServerUrl();
	}
	
}

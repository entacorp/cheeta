package io.cheeta.server.web.page.serverinit;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.UserService;
import io.cheeta.server.model.User;
import io.cheeta.server.security.SecurityUtils;
import io.cheeta.server.util.ManualConfigStep;
import io.cheeta.server.util.init.InitStage;
import io.cheeta.server.util.init.ManualConfig;
import io.cheeta.server.web.WebSession;
import io.cheeta.server.web.component.wizard.DefaultEndActionsPanel;
import io.cheeta.server.web.component.wizard.WizardPanel;
import io.cheeta.server.web.page.HomePage;
import io.cheeta.server.web.page.simple.SimplePage;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.wicket.RestartResponseException;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.head.OnDomReadyHeaderItem;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import static io.cheeta.server.web.translation.Translation._T;

import java.util.ArrayList;
import java.util.List;

public class ServerInitPage extends SimplePage {

	private final InitStage initStage;
	
	public ServerInitPage(PageParameters params) {
		super(params);
		
		initStage = SerializationUtils.clone(Cheeta.getInstance().getInitStage());
		if (initStage == null) {
			continueToOriginalDestination();
			throw new RestartResponseException(getApplication().getHomePage());
		}
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		if (!initStage.getManualConfigs().isEmpty()) {
			List<ManualConfigStep> configSteps = new ArrayList<ManualConfigStep>();
			for (ManualConfig each: initStage.getManualConfigs())
				configSteps.add(new ManualConfigStep(each));
			add(new WizardPanel("wizard", configSteps) {

				@Override
				protected WebMarkupContainer newEndActions(String componentId) {
					return new DefaultEndActionsPanel(componentId, this) {
						
						@Override
						protected void finished() {
							while (true) {
								var initStage = Cheeta.getInstance().getInitStage();
								if (initStage == null || initStage.getManualConfigs().isEmpty()) {
									break;
								} else  {
									try {
										Thread.sleep(10);
									} catch (InterruptedException e) {
										throw new RuntimeException(e);
									}
								}
							}
							WebSession.get().logout();
							User root = Cheeta.getInstance(UserService.class).getRoot();
							SecurityUtils.getSubject().runAs(root.getPrincipals());
							throw new RestartResponseException(HomePage.class);
						}
						
					};
				}

				
			});
		} else {
			add(new WebMarkupContainer("wizard").setVisible(false));
		}
	}
	
	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		if (initStage.getManualConfigs().isEmpty())
			response.render(OnDomReadyHeaderItem.forScript("$('.server-init').addClass('inited');"));
	}

	@Override
	protected int getPageRefreshInterval() {
		if (initStage.getManualConfigs().isEmpty())
			return 1;
		else
			return 0;
	}

	@Override
	protected String getTitle() {
		return _T(initStage.getMessage());
	}

	@Override
	protected String getSubTitle() {
		return null;
	}

}

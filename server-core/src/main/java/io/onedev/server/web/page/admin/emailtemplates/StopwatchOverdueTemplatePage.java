package io.cheeta.server.web.page.admin.emailtemplates;

import static io.cheeta.server.model.support.administration.emailtemplates.EmailTemplates.DEFAULT_STOPWATCH_OVERDUE;
import static io.cheeta.server.model.support.administration.emailtemplates.EmailTemplates.PROP_STOPWATCH_OVERDUE;
import static io.cheeta.server.web.translation.Translation._T;

import java.text.MessageFormat;
import java.util.Map;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.request.mapper.parameter.PageParameters;

import io.cheeta.server.util.CollectionUtils;

public class StopwatchOverdueTemplatePage extends AbstractTemplatePage {

	public StopwatchOverdueTemplatePage(PageParameters params) {
		super(params);
	}

	@Override
	protected String getPropertyName() {
		return PROP_STOPWATCH_OVERDUE;
	}

	@Override
	protected String getDefaultTemplate() {
		return DEFAULT_STOPWATCH_OVERDUE;
	}

	@Override
	protected String getHelpText() {
		return MessageFormat.format(_T("A {0} used as body of issue stopwatch overdue notification email"), GROOVY_TEMPLATE_LINK);
	}

	@Override
	protected Map<String, String> getVariableHelp() {
		return CollectionUtils.newLinkedHashMap(
				"stopwatch", _T("<a href='https://code.cheeta.io/cheeta/server/~files/main/server-core/src/main/java/io/cheeta/server/model/Stopwatch.java'>Stopwatch</a> overdue"));
	}

	@Override
	protected Component newTopbarTitle(String componentId) {
		return new Label(componentId, _T("Issue Stopwatch Overdue Notification Template"));
	}

}
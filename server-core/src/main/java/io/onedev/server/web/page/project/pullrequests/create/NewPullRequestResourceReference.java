package io.cheeta.server.web.page.project.pullrequests.create;

import java.util.List;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;

import io.cheeta.server.web.asset.revisioncompare.RevisionCompareCssResourceReference;
import io.cheeta.server.web.page.base.BaseDependentCssResourceReference;
import io.cheeta.server.web.page.base.BaseDependentResourceReference;

public class NewPullRequestResourceReference extends BaseDependentResourceReference {

	private static final long serialVersionUID = 1L;

	public NewPullRequestResourceReference() {
		super(NewPullRequestResourceReference.class, "new-pull-request.js");
	}

	@Override
	public List<HeaderItem> getDependencies() {
		List<HeaderItem> dependencies = super.getDependencies();
		dependencies.add(CssHeaderItem.forReference(new BaseDependentCssResourceReference(
			NewPullRequestResourceReference.class, "new-pull-request.css")));
		dependencies.add(CssHeaderItem.forReference(new RevisionCompareCssResourceReference()));
		return dependencies;
	}

}

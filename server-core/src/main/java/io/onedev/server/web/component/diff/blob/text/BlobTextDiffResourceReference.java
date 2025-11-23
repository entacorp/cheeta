package io.cheeta.server.web.component.diff.blob.text;

import java.util.List;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;
import org.apache.wicket.request.resource.CssResourceReference;

import io.cheeta.server.web.asset.clipboard.ClipboardResourceReference;
import io.cheeta.server.web.asset.codeproblem.CodeProblemResourceReference;
import io.cheeta.server.web.asset.commentindicator.CommentIndicatorCssResourceReference;
import io.cheeta.server.web.asset.diff.DiffResourceReference;
import io.cheeta.server.web.asset.doneevents.DoneEventsResourceReference;
import io.cheeta.server.web.asset.hover.HoverResourceReference;
import io.cheeta.server.web.asset.selectionpopover.SelectionPopoverResourceReference;
import io.cheeta.server.web.page.base.BaseDependentResourceReference;

public class BlobTextDiffResourceReference extends BaseDependentResourceReference {

	private static final long serialVersionUID = 1L;

	public BlobTextDiffResourceReference() {
		super(BlobTextDiffResourceReference.class, "blob-text-diff.js");
	}

	@Override
	public List<HeaderItem> getDependencies() {
		List<HeaderItem> dependencies = super.getDependencies();
		dependencies.add(JavaScriptHeaderItem.forReference(new DiffResourceReference()));
		dependencies.add(JavaScriptHeaderItem.forReference(new DoneEventsResourceReference()));
		dependencies.add(JavaScriptHeaderItem.forReference(new HoverResourceReference()));
		dependencies.add(JavaScriptHeaderItem.forReference(new SelectionPopoverResourceReference()));
		dependencies.add(JavaScriptHeaderItem.forReference(new ClipboardResourceReference()));
		dependencies.add(JavaScriptHeaderItem.forReference(new CodeProblemResourceReference()));
		dependencies.add(CssHeaderItem.forReference(
				new CssResourceReference(BlobTextDiffPanel.class, "blob-text-diff.css")));
		dependencies.add(CssHeaderItem.forReference(new CommentIndicatorCssResourceReference()));
		return dependencies;
	}

}

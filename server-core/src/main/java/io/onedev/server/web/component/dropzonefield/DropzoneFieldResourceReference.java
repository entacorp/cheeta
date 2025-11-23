package io.cheeta.server.web.component.dropzonefield;

import java.util.List;

import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.HeaderItem;
import org.apache.wicket.markup.head.JavaScriptHeaderItem;

import io.cheeta.server.web.asset.dropzone.DropzoneResourceReference;
import io.cheeta.server.web.page.base.BaseDependentCssResourceReference;
import io.cheeta.server.web.page.base.BaseDependentResourceReference;

public class DropzoneFieldResourceReference extends BaseDependentResourceReference {

	public DropzoneFieldResourceReference() {
		super(DropzoneFieldResourceReference.class, "dropzone-field.js");
	}

	@Override
	public List<HeaderItem> getDependencies() {
		List<HeaderItem> dependencies = super.getDependencies();
		dependencies.add(JavaScriptHeaderItem.forReference(new DropzoneResourceReference()));
		dependencies.add(CssHeaderItem.forReference(
				new BaseDependentCssResourceReference(DropzoneField.class, "dropzone-field.css")));
		return dependencies;
	}

}

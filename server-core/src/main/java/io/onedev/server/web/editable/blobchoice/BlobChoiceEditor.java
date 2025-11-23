package io.cheeta.server.web.editable.blobchoice;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.util.convert.ConversionException;

import io.cheeta.server.git.BlobIdent;
import io.cheeta.server.git.BlobIdentFilter;
import io.cheeta.server.util.ComponentContext;
import io.cheeta.server.util.ProjectScopedCommit;
import io.cheeta.server.util.ReflectionUtils;
import io.cheeta.commons.utils.match.PathMatcher;
import io.cheeta.server.util.patternset.PatternSet;
import io.cheeta.server.web.component.blob.BlobPicker;
import io.cheeta.server.web.editable.PropertyDescriptor;
import io.cheeta.server.web.editable.PropertyEditor;
import io.cheeta.server.annotation.BlobChoice;

public class BlobChoiceEditor extends PropertyEditor<String> {

	private final IModel<ProjectScopedCommit> commitModel = new LoadableDetachableModel<ProjectScopedCommit>() {

		@Override
		protected ProjectScopedCommit load() {
			ComponentContext.push(new ComponentContext(BlobChoiceEditor.this));
			try {
				BlobChoice blobChoice = descriptor.getPropertyGetter().getAnnotation(BlobChoice.class);
				return (ProjectScopedCommit) ReflectionUtils.invokeStaticMethod(
						descriptor.getBeanClass(), blobChoice.commitProvider());
			} finally {
				ComponentContext.pop();
			}
		}
		
	};
	
	private final IModel<PatternSet> patternsModel = new LoadableDetachableModel<PatternSet>() {

		@Override
		protected PatternSet load() {
			ComponentContext.push(new ComponentContext(BlobChoiceEditor.this));
			try {
				BlobChoice blobChoice = descriptor.getPropertyGetter().getAnnotation(BlobChoice.class);
				return PatternSet.parse(blobChoice.patterns());
			} finally {
				ComponentContext.pop();
			}
		}
		
	};
	
	private String blobPath;
	
	public BlobChoiceEditor(String id, PropertyDescriptor propertyDescriptor, 
			IModel<String> propertyModel) {
		super(id, propertyDescriptor, propertyModel);
		blobPath = propertyModel.getObject();
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		add(newBlobPicker());
	}
	
	private BlobPicker newBlobPicker() {
		return new BlobPicker("picker", commitModel, blobPath) {

			@Override
			protected void onSelect(AjaxRequestTarget target, String blobPath) {
				BlobChoiceEditor.this.blobPath = blobPath; 
				BlobPicker blobPicker = newBlobPicker();
				getParent().replace(blobPicker);
				target.add(blobPicker);
				onPropertyUpdating(target);				
			}
			
			@Override
			protected BlobIdentFilter getBlobIdentFilter() {
				
				return new BlobIdentFilter() {

					@Override
					public boolean filter(BlobIdent blobIdent) {
						return blobIdent.isTree() || patternsModel.getObject().matches(new PathMatcher(), blobIdent.path); 
					}
					
				};
			}
			
		};		
	}

	@Override
	protected void onDetach() {
		commitModel.detach();
		patternsModel.detach();
		super.onDetach();
	}

	@Override
	protected String convertInputToValue() throws ConversionException {
		return blobPath;
	}

	@Override
	public boolean needExplicitSubmit() {
		return false;
	}

}

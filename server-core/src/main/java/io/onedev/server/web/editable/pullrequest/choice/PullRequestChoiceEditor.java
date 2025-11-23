package io.cheeta.server.web.editable.pullrequest.choice;

import static io.cheeta.server.web.translation.Translation._T;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.ConversionException;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.PullRequestService;
import io.cheeta.server.model.Project;
import io.cheeta.server.model.PullRequest;
import io.cheeta.server.web.component.pullrequest.choice.PullRequestChoiceProvider;
import io.cheeta.server.web.component.pullrequest.choice.PullRequestSingleChoice;
import io.cheeta.server.web.editable.PropertyDescriptor;
import io.cheeta.server.web.editable.PropertyEditor;
import io.cheeta.server.web.util.ProjectAware;

public class PullRequestChoiceEditor extends PropertyEditor<Long> {

	private PullRequestSingleChoice input;
	
	public PullRequestChoiceEditor(String id, PropertyDescriptor propertyDescriptor, 
			IModel<Long> propertyModel) {
		super(id, propertyDescriptor, propertyModel);
	}

	private Project getProject() {
		ProjectAware projectAware = findParent(ProjectAware.class);
		if (projectAware != null)
			return projectAware.getProject();		
		else
			return null;
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		PullRequest request;
		if (getModelObject() != null)
			request = Cheeta.getInstance(PullRequestService.class).get(getModelObject());
		else
			request = null;
		
		PullRequestChoiceProvider choiceProvider = new PullRequestChoiceProvider() {


			@Override
			protected Project getProject() {
				return PullRequestChoiceEditor.this.getProject();
			}
			
		};
    	input = new PullRequestSingleChoice("input", Model.of(request), choiceProvider) {

    		@Override
			protected void onInitialize() {
				super.onInitialize();
				getSettings().configurePlaceholder(descriptor);
				getSettings().setAllowClear(!descriptor.isPropertyRequired());
			}
    		
    	};
        
        input.setLabel(Model.of(_T(getDescriptor().getDisplayName())));
        
		input.add(new AjaxFormComponentUpdatingBehavior("change"){

			@Override
			protected void onUpdate(AjaxRequestTarget target) {
				onPropertyUpdating(target);
			}
			
		});
		add(input);
	}

	@Override
	protected Long convertInputToValue() throws ConversionException {
		PullRequest request = input.getConvertedInput();
		if (request != null)
			return request.getId();
		else
			return null;
	}

	@Override
	public boolean needExplicitSubmit() {
		return false;
	}

}

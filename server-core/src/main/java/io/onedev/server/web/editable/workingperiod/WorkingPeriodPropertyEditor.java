package io.cheeta.server.web.editable.workingperiod;

import io.cheeta.commons.utils.StringUtils;
import io.cheeta.server.Cheeta;
import io.cheeta.server.service.SettingService;
import io.cheeta.server.model.support.issue.TimeTrackingSetting;
import io.cheeta.server.web.behavior.OnTypingDoneBehavior;
import io.cheeta.server.web.editable.PropertyDescriptor;
import io.cheeta.server.web.editable.PropertyEditor;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.ConversionException;

import static io.cheeta.server.web.translation.Translation._T;

import javax.validation.ValidationException;

public class WorkingPeriodPropertyEditor extends PropertyEditor<Integer> {

	private TextField<String> input;
	
	public WorkingPeriodPropertyEditor(String id, PropertyDescriptor propertyDescriptor, IModel<Integer> propertyModel) {
		super(id, propertyDescriptor, propertyModel);
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();
		
		String period;
		if (getModelObject() != null) 
			period = getTimeTrackingSetting().formatWorkingPeriod(getModelObject(), false);
		else
			period = null;
		input = new TextField<>("input", Model.of(period));
		add(input);
		input.setLabel(Model.of(_T(getDescriptor().getDisplayName())));
		
		input.add(new OnTypingDoneBehavior() {

			@Override
			protected void onTypingDone(AjaxRequestTarget target) {
				onPropertyUpdating(target);
			}
			
		});
		input.add(newPlaceholderModifier());
	}

	@Override
	protected Integer convertInputToValue() throws ConversionException {
		if (StringUtils.isNotBlank(input.getConvertedInput())) {
			try {
				return getTimeTrackingSetting().parseWorkingPeriod(input.getConvertedInput());
			} catch (ValidationException e) {
				throw new ConversionException(e.getMessage());
			}
		} else {
			return null;
		}
	}
	
	private TimeTrackingSetting getTimeTrackingSetting() {
		return Cheeta.getInstance(SettingService.class).getIssueSetting().getTimeTrackingSetting();
	}

	@Override
	public boolean needExplicitSubmit() {
		return true;
	}

}

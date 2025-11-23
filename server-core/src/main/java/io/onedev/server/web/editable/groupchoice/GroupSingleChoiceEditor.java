package io.cheeta.server.web.editable.groupchoice;

import static io.cheeta.server.web.translation.Translation._T;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import io.cheeta.server.util.ComponentContext;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.convert.ConversionException;

import com.google.common.base.Preconditions;

import io.cheeta.server.Cheeta;
import io.cheeta.server.service.GroupService;
import io.cheeta.server.model.Group;
import io.cheeta.server.util.ReflectionUtils;
import io.cheeta.server.web.component.groupchoice.GroupSingleChoice;
import io.cheeta.server.web.editable.PropertyDescriptor;
import io.cheeta.server.web.editable.PropertyEditor;
import io.cheeta.server.annotation.GroupChoice;

public class GroupSingleChoiceEditor extends PropertyEditor<String> {
	
	private GroupSingleChoice input;
	
	public GroupSingleChoiceEditor(String id, PropertyDescriptor propertyDescriptor, IModel<String> propertyModel) {
		super(id, propertyDescriptor, propertyModel);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void onInitialize() {
		super.onInitialize();

		List<Group> choices = new ArrayList<>();

		ComponentContext componentContext = new ComponentContext(this);
		ComponentContext.push(componentContext);
		try {
			GroupChoice groupChoice = descriptor.getPropertyGetter().getAnnotation(GroupChoice.class);
			Preconditions.checkNotNull(groupChoice);
			if (groupChoice.value().length() != 0) {
				choices.addAll((List<Group>) ReflectionUtils
						.invokeStaticMethod(descriptor.getBeanClass(), groupChoice.value()));
			} else {
				choices.addAll(Cheeta.getInstance(GroupService.class).query());
				choices.sort(Comparator.comparing(Group::getName));
			}
		} finally {
			ComponentContext.pop();
		}
		Group group;
		if (getModelObject() != null)
			group = Cheeta.getInstance(GroupService.class).find(getModelObject());
		else
			group = null;
		
		if (group != null && !choices.contains(group))
			group = null;

    	input = new GroupSingleChoice("input", Model.of(group), Model.of(choices)) {

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
	protected String convertInputToValue() throws ConversionException {
		Group group = input.getConvertedInput();
		if (group != null)
			return group.getName();
		else
			return null;
	}

	@Override
	public boolean needExplicitSubmit() {
		return false;
	}

}

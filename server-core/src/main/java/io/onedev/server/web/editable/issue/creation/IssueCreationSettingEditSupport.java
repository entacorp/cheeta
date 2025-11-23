package io.cheeta.server.web.editable.issue.creation;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.model.IModel;

import io.cheeta.server.model.support.administration.IssueCreationSetting;
import io.cheeta.server.util.ReflectionUtils;
import io.cheeta.server.web.editable.EditSupport;
import io.cheeta.server.web.editable.PropertyContext;
import io.cheeta.server.web.editable.PropertyDescriptor;
import io.cheeta.server.web.editable.PropertyEditor;
import io.cheeta.server.web.editable.PropertyViewer;

public class IssueCreationSettingEditSupport implements EditSupport {

	@Override
	public PropertyContext<?> getEditContext(PropertyDescriptor descriptor) {
		if (List.class.isAssignableFrom(descriptor.getPropertyClass())) {
			Class<?> elementClass = ReflectionUtils.getCollectionElementClass(descriptor.getPropertyGetter().getGenericReturnType());
			if (elementClass == IssueCreationSetting.class) {
				return new PropertyContext<List<Serializable>>(descriptor) {

					@Override
					public PropertyViewer renderForView(String componentId, final IModel<List<Serializable>> model) {
						throw new UnsupportedOperationException();
					}

					@Override
					public PropertyEditor<List<Serializable>> renderForEdit(String componentId, IModel<List<Serializable>> model) {
						return new IssueCreationSettingListEditPanel(componentId, descriptor, model);
					}
					
				};
			}
		}
		return null;
	}

	@Override
	public int getPriority() {
		return 0;
	}
	
}

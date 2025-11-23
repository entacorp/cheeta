package io.cheeta.server.web.editable.servicelocator;

import java.io.Serializable;
import java.lang.reflect.AnnotatedElement;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.model.IModel;

import io.cheeta.server.model.support.administration.jobexecutor.ServiceLocator;
import io.cheeta.server.util.ReflectionUtils;
import io.cheeta.server.web.editable.EditSupport;
import io.cheeta.server.web.editable.EmptyValueLabel;
import io.cheeta.server.web.editable.PropertyContext;
import io.cheeta.server.web.editable.PropertyDescriptor;
import io.cheeta.server.web.editable.PropertyEditor;
import io.cheeta.server.web.editable.PropertyViewer;

public class ServiceLocatorListEditSupport implements EditSupport {

	@Override
	public PropertyContext<?> getEditContext(PropertyDescriptor descriptor) {
		if (List.class.isAssignableFrom(descriptor.getPropertyClass())) {
			Class<?> elementClass = ReflectionUtils.getCollectionElementClass(descriptor.getPropertyGetter().getGenericReturnType());
			if (elementClass == ServiceLocator.class) {
				return new PropertyContext<List<Serializable>>(descriptor) {

					@Override
					public PropertyViewer renderForView(String componentId, final IModel<List<Serializable>> model) {
						return new PropertyViewer(componentId, descriptor) {

							@Override
							protected Component newContent(String id, PropertyDescriptor propertyDescriptor) {
								if (model.getObject() != null) {
									return new ServiceLocatorListViewPanel(id, model.getObject());
								} else { 
									return new EmptyValueLabel(id) {

										@Override
										protected AnnotatedElement getElement() {
											return propertyDescriptor.getPropertyGetter();
										}
										
									};
								}
							}
							
						};
					}

					@Override
					public PropertyEditor<List<Serializable>> renderForEdit(String componentId, IModel<List<Serializable>> model) {
						return new ServiceLocatorListEditPanel(componentId, descriptor, model);
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
